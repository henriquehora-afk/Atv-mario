package com.biblioteca.api.pdf;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * Valida links externos antes de a API acessá-los. Só passam links http/https
 * cujo destino seja um endereço público da internet (proteção contra SSRF).
 */
public final class UrlSegura {

	private UrlSegura() {
	}

	/** Valida só o formato (sem consultar DNS). */
	public static URI validarFormato(String url) {
		if (url == null || url.isBlank()) {
			throw new UrlInvalidaException("Informe o link do PDF.");
		}
		URI uri;
		try {
			uri = new URI(url.trim());
		} catch (URISyntaxException e) {
			throw new UrlInvalidaException("Link inválido.");
		}
		String esquema = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
		if (!esquema.equals("http") && !esquema.equals("https")) {
			throw new UrlInvalidaException("O link precisa começar com http:// ou https://.");
		}
		if (uri.getHost() == null || uri.getHost().isBlank()) {
			throw new UrlInvalidaException("Link inválido: endereço do site ausente.");
		}
		if (uri.getRawUserInfo() != null) {
			throw new UrlInvalidaException("Links com usuário e senha não são permitidos.");
		}
		return uri;
	}

	/** Valida o formato e confere se o destino é um endereço público. */
	public static URI validar(String url) {
		URI uri = validarFormato(url);
		InetAddress[] enderecos;
		try {
			enderecos = InetAddress.getAllByName(uri.getHost());
		} catch (UnknownHostException e) {
			throw new UrlInvalidaException("Não foi possível encontrar o site " + uri.getHost() + ".");
		}
		for (InetAddress endereco : enderecos) {
			if (!enderecoPublico(endereco)) {
				throw new UrlInvalidaException("Links para endereços internos ou locais não são permitidos.");
			}
		}
		return uri;
	}

	static boolean enderecoPublico(InetAddress endereco) {
		if (endereco.isAnyLocalAddress() || endereco.isLoopbackAddress() || endereco.isLinkLocalAddress()
				|| endereco.isSiteLocalAddress() || endereco.isMulticastAddress()) {
			return false;
		}
		byte[] b = endereco.getAddress();
		if (endereco instanceof Inet4Address) {
			int primeiro = b[0] & 0xFF;
			int segundo = b[1] & 0xFF;
			return primeiro != 0                                   // 0.0.0.0/8
					&& !(primeiro == 100 && (segundo & 0xC0) == 64) // 100.64.0.0/10 (CGNAT)
					&& !(primeiro == 192 && segundo == 0 && (b[2] & 0xFF) == 0) // 192.0.0.0/24
					&& primeiro < 240;                              // reservado
		}
		if (endereco instanceof Inet6Address) {
			return (b[0] & 0xFE) != 0xFC; // fc00::/7 (rede local IPv6)
		}
		return false;
	}
}