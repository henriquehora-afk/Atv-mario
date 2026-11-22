package com.biblioteca.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.api.dto.request.ChangePasswordRequest;
import com.biblioteca.api.dto.request.UpdateMeRequest;
import com.biblioteca.api.dto.response.MessageResponse;
import com.biblioteca.api.dto.response.UserResponse;
import com.biblioteca.api.security.UsuarioLogado;
import com.biblioteca.api.service.UsuarioService;

import jakarta.validation.Valid;

/** Dados do usuário logado. O id vem do token, nunca da URL. */
@RestController
@RequestMapping("/api/v1/users/me")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping
	public UserResponse me(@UsuarioLogado Long usuarioId) {
		return usuarioService.buscarPorId(usuarioId);
	}

	@PutMapping
	public UserResponse atualizar(@UsuarioLogado Long usuarioId, @Valid @RequestBody UpdateMeRequest request) {
		return usuarioService.atualizar(usuarioId, request);
	}

	@PutMapping("/password")
	public MessageResponse alterarSenha(
			@UsuarioLogado Long usuarioId,
			@Valid @RequestBody ChangePasswordRequest request) {
		usuarioService.alterarSenha(usuarioId, request);
		return new MessageResponse("Senha alterada com sucesso.");
	}
}