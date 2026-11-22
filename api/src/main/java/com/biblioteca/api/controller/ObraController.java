package com.biblioteca.api.controller;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.api.dto.request.CriarObraRequest;
import com.biblioteca.api.dto.response.ObraResponse;
import com.biblioteca.api.model.ArquivoPdf;
import com.biblioteca.api.security.UsuarioLogado;
import com.biblioteca.api.service.ObraService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/obras")
public class ObraController {

	private final ObraService obraService;

	public ObraController(ObraService obraService) {
		this.obraService = obraService;
	}

	/** Busca: GET /api/v1/obras?q=transformer&tipo=Artigo&anoDe=2015&anoAte=2020 */
	@GetMapping
	public List<ObraResponse> pesquisar(
			@RequestParam(name = "q", required = false) String q,
			@RequestParam(name = "tipo", required = false) String tipo,
			@RequestParam(name = "anoDe", required = false) Integer anoDe,
			@RequestParam(name = "anoAte", required = false) Integer anoAte) {
		return obraService.pesquisar(q, tipo, anoDe, anoAte);
	}

	@GetMapping("/{id}")
	public ObraResponse buscarPorId(@PathVariable("id") Long id) {
		return obraService.buscarPorId(id);
	}

	@PostMapping
	public ResponseEntity<ObraResponse> cadastrar(
			@UsuarioLogado Long usuarioId,
			@Valid @RequestBody CriarObraRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(obraService.cadastrar(usuarioId, request));
	}

	/** download=false abre no leitor do navegador; download=true pede para salvar. */
	@GetMapping("/{id}/pdf")
	public ResponseEntity<byte[]> pdf(
			@PathVariable("id") Long id,
			@RequestParam(name = "download", defaultValue = "false") boolean download) {
		ArquivoPdf arquivo = obraService.obterPdf(id);
		ContentDisposition disposicao = (download ? ContentDisposition.attachment() : ContentDisposition.inline())
				.filename(arquivo.nomeArquivo(), StandardCharsets.UTF_8)
				.build();
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, disposicao.toString())
				.header("X-Content-Type-Options", "nosniff")
				.cacheControl(CacheControl.maxAge(Duration.ofMinutes(10)).cachePrivate())
				.body(arquivo.conteudo());
	}
}