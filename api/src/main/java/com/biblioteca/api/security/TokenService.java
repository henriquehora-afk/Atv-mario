package com.biblioteca.api.security;

import java.time.Duration;
import java.util.Optional;


public interface TokenService {

	String gerarAccessToken(Long usuarioId);

	String gerarRefreshToken(Long usuarioId);

	/** Devolve o id do usuário se o token for um access token válido e não expirado. */
	Optional<Long> validarAccessToken(String token);

	/** Devolve o id do usuário se o token for um refresh token válido e não expirado. */
	Optional<Long> validarRefreshToken(String token);

	Duration duracaoRefreshToken();
}