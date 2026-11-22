package com.biblioteca.api.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.api.dto.request.LoginRequest;
import com.biblioteca.api.dto.request.RegisterRequest;
import com.biblioteca.api.dto.response.AuthResponse;
import com.biblioteca.api.dto.response.UserResponse;
import com.biblioteca.api.exception.FuncionalidadeIndisponivelException;
import com.biblioteca.api.security.NaoAutenticadoException;
import com.biblioteca.api.security.TokenService;
import com.biblioteca.api.service.UsuarioService;

import jakarta.validation.Valid;

/**
 * Autenticação. O access token (curto) vai no corpo e o front o guarda em memória;
 * o refresh token (longo) vai num cookie HttpOnly, inacessível ao JavaScript.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private static final String COOKIE_REFRESH = "refresh_token";
	private static final String CAMINHO_COOKIE = "/api/v1/auth";

	private final UsuarioService usuarioService;
	private final TokenService tokenService;
	private final boolean cookieSeguro;

	public AuthController(
			UsuarioService usuarioService,
			TokenService tokenService,
			@Value("${app.cookie.secure:false}") boolean cookieSeguro) {
		this.usuarioService = usuarioService;
		this.tokenService = tokenService;
		this.cookieSeguro = cookieSeguro;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegisterRequest request) {
		UserResponse usuario = usuarioService.registrar(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new AuthResponse(null, usuario, "Cadastro realizado."));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return sessao(usuarioService.autenticar(request));
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refresh(
			@CookieValue(name = COOKIE_REFRESH, required = false) String refreshToken) {
		Long usuarioId = tokenService.validarRefreshToken(refreshToken)
				.orElseThrow(NaoAutenticadoException::new);
		return sessao(usuarioService.buscarPorId(usuarioId));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		return ResponseEntity.noContent()
				.header(HttpHeaders.SET_COOKIE, cookie("", Duration.ZERO).toString())
				.build();
	}

	@PostMapping("/password/forgot")
	public void esqueciSenha() {
		throw new FuncionalidadeIndisponivelException(
				"Recuperação de senha por e-mail ainda não está disponível. Fale com o administrador.");
	}

	@PostMapping("/password/reset")
	public void redefinirSenha() {
		throw new FuncionalidadeIndisponivelException(
				"Recuperação de senha por e-mail ainda não está disponível. Fale com o administrador.");
	}

	private ResponseEntity<AuthResponse> sessao(UserResponse usuario) {
		String refresh = tokenService.gerarRefreshToken(usuario.id());
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie(refresh, tokenService.duracaoRefreshToken()).toString())
				.body(new AuthResponse(tokenService.gerarAccessToken(usuario.id()), usuario, null));
	}

	private ResponseCookie cookie(String valor, Duration duracao) {
		return ResponseCookie.from(COOKIE_REFRESH, valor)
				.httpOnly(true)
				.secure(cookieSeguro) // true em produção (HTTPS)
				.sameSite("Lax")
				.path(CAMINHO_COOKIE)
				.maxAge(duracao)
				.build();
	}
}