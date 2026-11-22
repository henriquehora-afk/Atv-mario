package com.biblioteca.api.service;

import java.text.Normalizer;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.biblioteca.api.dto.request.CriarObraRequest;
import com.biblioteca.api.dto.response.ObraResponse;
import com.biblioteca.api.exception.ObraNaoEncontradaException;
import com.biblioteca.api.exception.ObraSemPdfException;
import com.biblioteca.api.model.ArquivoPdf;
import com.biblioteca.api.model.FiltroObras;
import com.biblioteca.api.model.Obra;
import com.biblioteca.api.pdf.PdfDownloader;
import com.biblioteca.api.pdf.UrlSegura;
import com.biblioteca.api.repository.ObraRepository;

/** Regras das obras. Depende das abstrações ObraRepository e PdfDownloader. */
@Service
public class ObraService {

	private static final int LIMITE_RESULTADOS = 100;
	private static final int MAX_TERMOS = 10;

	private final ObraRepository obraRepository;
	private final PdfDownloader pdfDownloader;

	public ObraService(ObraRepository obraRepository, PdfDownloader pdfDownloader) {
		this.obraRepository = obraRepository;
		this.pdfDownloader = pdfDownloader;
	}

	public List<ObraResponse> pesquisar(String texto, String tipo, Integer anoDe, Integer anoAte) {
		List<String> termos = texto == null ? List.of() : Arrays.stream(texto.trim().split("\\s+"))
				.filter(t -> !t.isBlank())
				.map(t -> t.length() > 100 ? t.substring(0, 100) : t)
				.limit(MAX_TERMOS)
				.toList();
		String tipoFiltro = tipo == null || tipo.isBlank() || tipo.equals("Todos") ? null : tipo;
		return obraRepository.pesquisar(new FiltroObras(termos, tipoFiltro, anoDe, anoAte, LIMITE_RESULTADOS))
				.stream().map(ObraResponse::de).toList();
	}

	public ObraResponse buscarPorId(Long id) {
		return ObraResponse.de(buscar(id));
	}

	public ObraResponse cadastrar(Long usuarioId, CriarObraRequest request) {
		// Confere já no cadastro se o link aponta para um endereço público.
		String pdfUrl = UrlSegura.validar(request.pdfUrl()).toString();
		String paginaUrl = request.url() == null || request.url().isBlank()
				? pdfUrl
				: UrlSegura.validarFormato(request.url()).toString();
		Obra nova = new Obra(
				null,
				"Cadastro manual",
				request.titulo().trim(),
				limpar(request.autores()),
				request.ano(),
				request.tipo(),
				vazioParaNulo(request.resumo()),
				vazioParaNulo(request.doi()),
				paginaUrl,
				limpar(request.palavrasChave()),
				pdfUrl,
				usuarioId,
				Instant.now());
		return ObraResponse.de(obraRepository.salvar(nova));
	}

	/** Busca o PDF na fonte original, no lugar do navegador do usuário. */
	public ArquivoPdf obterPdf(Long id) {
		Obra obra = buscar(id);
		if (!obra.temPdf()) {
			throw new ObraSemPdfException();
		}
		return new ArquivoPdf(nomeArquivo(obra), pdfDownloader.baixar(obra.pdfUrl()));
	}

	private Obra buscar(Long id) {
		return obraRepository.buscarPorId(id).orElseThrow(ObraNaoEncontradaException::new);
	}

	/** Ex.: "Attention Is All You Need" (2017) -> attention-is-all-you-need-2017.pdf */
	static String nomeArquivo(Obra obra) {
		String base = Normalizer.normalize(obra.titulo(), Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("(^-|-$)", "");
		if (base.length() > 80) {
			base = base.substring(0, 80).replaceAll("-$", "");
		}
		return (base.isEmpty() ? "obra" : base) + "-" + obra.ano() + ".pdf";
	}

	private static List<String> limpar(List<String> valores) {
		return valores == null ? List.of() : valores.stream().map(String::trim).filter(v -> !v.isEmpty()).distinct().toList();
	}

	private static String vazioParaNulo(String valor) {
		return valor == null || valor.isBlank() ? null : valor.trim();
	}
}