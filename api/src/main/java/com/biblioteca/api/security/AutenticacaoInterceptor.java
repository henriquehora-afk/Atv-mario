package com.biblioteca.api.security;

import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Exige "Authorization: Bearer <token>" nas rotas protegidas. */
public class AutenticacaoInterceptor implements HandlerInterceptor {

	public static final String ATRIBUTO_USUARIO_ID = "biblioteca.usuarioId";
	private static final String PREFIXO = "Bearer ";

	private final TokenService tokenService;

	public AutenticacaoInterceptor(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (CorsUtils.isPreFlightRequest(request)) {
			return true;
		}
		String cabecalho = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (cabecalho == null || !cabecalho.startsWith(PREFIXO)) {
			throw new NaoAutenticadoException();
		}
		Long usuarioId = tokenService.validarAccessToken(cabecalho.substring(PREFIXO.length()).trim())
				.orElseThrow(NaoAutenticadoException::new);
		request.setAttribute(ATRIBUTO_USUARIO_ID, usuarioId);
		return true;
	}
}