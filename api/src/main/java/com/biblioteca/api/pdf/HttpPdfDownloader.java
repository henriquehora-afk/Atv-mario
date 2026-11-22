package com.biblioteca.api.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * Baixa PDFs de links externos: só endereços públicos (inclusive após
 * redirecionamentos), limite de tamanho, tempo máximo e conferência do formato.
 */
@Component
public class HttpPdfDownloader implements PdfDownloader {

	private static final int TAMANHO_MAXIMO = 30 * 1024 * 1024; // 30 MB
	private static final int MAX_REDIRECIONAMENTOS = 3;
	private static final Set<Integer> REDIRECIONAMENTOS = Set.of(301, 302, 303, 307, 308);

	private final HttpClient cliente = HttpClient.newBuilder()
			.followRedirects(HttpClient.Redirect.NEVER) // seguimos manualmente, validando cada destino
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	@Override
	public byte[] baixar(String url) {
		URI uri = UrlSegura.validar(url);
		for (int tentativa = 0; tentativa <= MAX_REDIRECIONAMENTOS; tentativa++) {
			HttpResponse<InputStream> resposta = enviar(uri);
			int status = resposta.statusCode();
			if (REDIRECIONAMENTOS.contains(status)) {
				fechar(resposta.body());
				String destino = resposta.headers().firstValue("Location")
						.orElseThrow(() -> new PdfIndisponivelException("A fonte redirecionou sem informar o destino."));
				uri = UrlSegura.validar(uri.resolve(destino).toString());
				continue;
			}
			if (status != 200) {
				fechar(resposta.body());
				throw new PdfIndisponivelException("A fonte do PDF respondeu com erro (HTTP " + status + ").");
			}
			long tamanhoInformado = resposta.headers().firstValueAsLong("Content-Length").orElse(-1);
			if (tamanhoInformado > TAMANHO_MAXIMO) {
				fechar(resposta.body());
				throw new PdfIndisponivelException("O PDF é maior que o limite de 30 MB.");
			}
			byte[] dados = lerComLimite(resposta.body());
			if (!pareceSerPdf(dados)) {
				throw new PdfIndisponivelException("O link não aponta para um arquivo PDF.");
			}
			return dados;
		}
		throw new PdfIndisponivelException("A fonte fez redirecionamentos demais.");
	}

	private HttpResponse<InputStream> enviar(URI uri) {
		HttpRequest requisicao = HttpRequest.newBuilder(uri)
				.timeout(Duration.ofSeconds(30))
				.header("User-Agent", "BibliotecaAcademica/1.0")
				.header("Accept", "application/pdf,*/*;q=0.8")
				.GET()
				.build();
		try {
			return cliente.send(requisicao, HttpResponse.BodyHandlers.ofInputStream());
		} catch (IOException e) {
			throw new PdfIndisponivelException("Não foi possível acessar a fonte do PDF.");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new PdfIndisponivelException("O download do PDF foi interrompido.");
		}
	}

	private static byte[] lerComLimite(InputStream entrada) {
		try (entrada) {
			byte[] dados = entrada.readNBytes(TAMANHO_MAXIMO + 1);
			if (dados.length > TAMANHO_MAXIMO) {
				throw new PdfIndisponivelException("O PDF é maior que o limite de 30 MB.");
			}
			return dados;
		} catch (IOException e) {
			throw new PdfIndisponivelException("A conexão com a fonte do PDF caiu durante o download.");
		}
	}

	static boolean pareceSerPdf(byte[] dados) {
		// Todo PDF começa com "%PDF-" (às vezes depois de alguns bytes iniciais).
		int limite = Math.min(dados.length - 5, 1024);
		for (int i = 0; i <= limite; i++) {
			if (dados[i] == '%' && dados[i + 1] == 'P' && dados[i + 2] == 'D' && dados[i + 3] == 'F' && dados[i + 4] == '-') {
				return true;
			}
		}
		return false;
	}

	private static void fechar(InputStream corpo) {
		try {
			corpo.close();
		} catch (IOException ignorado) {
			// nada a fazer
		}
	}
}