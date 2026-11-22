package com.biblioteca.api.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Implementação de TokenService com JWT assinado em HMAC-SHA256 (HS256).
 * O campo "tipo" separa access token de refresh token.
 */
public class JwtTokenService implements TokenService {

	private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
	private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();
	private static final Base64.Decoder B64_DEC = Base64.getUrlDecoder();
	private static final Pattern SUB = Pattern.compile("\"sub\":\"(\\d{1,19})\"");
	private static final Pattern TIPO = Pattern.compile("\"tipo\":\"(access|refresh)\"");
	private static final Pattern EXP = Pattern.compile("\"exp\":(\\d{1,19})");

	private final byte[] segredo;
	private final Duration duracaoAccess;
	private final Duration duracaoRefresh;
	private final Clock clock;

	public JwtTokenService(byte[] segredo, Duration duracaoAccess, Duration duracaoRefresh, Clock clock) {
		if (segredo == null || segredo.length < 32) {
			throw new IllegalArgumentException("O segredo do JWT precisa ter pelo menos 32 bytes.");
		}
		this.segredo = segredo.clone();
		this.duracaoAccess = duracaoAccess;
		this.duracaoRefresh = duracaoRefresh;
		this.clock = clock;
	}

	@Override
	public String gerarAccessToken(Long usuarioId) {
		return gerar(usuarioId, "access", duracaoAccess);
	}

	@Override
	public String gerarRefreshToken(Long usuarioId) {
		return gerar(usuarioId, "refresh", duracaoRefresh);
	}

	@Override
	public Optional<Long> validarAccessToken(String token) {
		return validar(token, "access");
	}

	@Override
	public Optional<Long> validarRefreshToken(String token) {
		return validar(token, "refresh");
	}

	@Override
	public Duration duracaoRefreshToken() {
		return duracaoRefresh;
	}

	private String gerar(Long usuarioId, String tipo, Duration duracao) {
		long agora = clock.instant().getEpochSecond();
		String payload = "{\"sub\":\"" + usuarioId + "\",\"tipo\":\"" + tipo
				+ "\",\"iat\":" + agora + ",\"exp\":" + (agora + duracao.getSeconds()) + "}";
		String conteudo = b64(HEADER_JSON) + "." + b64(payload);
		return conteudo + "." + B64.encodeToString(assinar(conteudo));
	}

	private Optional<Long> validar(String token, String tipoEsperado) {
		if (token == null || token.length() > 2048) {
			return Optional.empty();
		}
		String[] partes = token.split("\\.");
		if (partes.length != 3) {
			return Optional.empty();
		}
		try {
			if (!HEADER_JSON.equals(new String(B64_DEC.decode(partes[0]), StandardCharsets.UTF_8))) {
				return Optional.empty();
			}
			byte[] assinaturaRecebida = B64_DEC.decode(partes[2]);
			byte[] assinaturaEsperada = assinar(partes[0] + "." + partes[1]);
			// Comparação em tempo constante
			if (!MessageDigest.isEqual(assinaturaRecebida, assinaturaEsperada)) {
				return Optional.empty();
			}
			String payload = new String(B64_DEC.decode(partes[1]), StandardCharsets.UTF_8);
			Matcher tipo = TIPO.matcher(payload);
			Matcher sub = SUB.matcher(payload);
			Matcher exp = EXP.matcher(payload);
			if (!tipo.find() || !tipo.group(1).equals(tipoEsperado) || !sub.find() || !exp.find()) {
				return Optional.empty();
			}
			if (Long.parseLong(exp.group(1)) <= clock.instant().getEpochSecond()) {
				return Optional.empty();
			}
			return Optional.of(Long.parseLong(sub.group(1)));
		} catch (IllegalArgumentException tokenMalformado) {
			return Optional.empty();
		}
	}

	private byte[] assinar(String conteudo) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(segredo, "HmacSHA256"));
			return mac.doFinal(conteudo.getBytes(StandardCharsets.US_ASCII));
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new IllegalStateException("HmacSHA256 indisponível.", e);
		}
	}

	private static String b64(String texto) {
		return B64.encodeToString(texto.getBytes(StandardCharsets.UTF_8));
	}
}