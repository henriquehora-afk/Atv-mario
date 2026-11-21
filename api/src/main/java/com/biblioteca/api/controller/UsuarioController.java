package com.biblioteca.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.api.dto.request.AtualizarUsuarioRequest;
import com.biblioteca.api.dto.request.CriarUsuarioRequest;
import com.biblioteca.api.dto.request.LoginRequest;
import com.biblioteca.api.dto.response.LoginResponse;
import com.biblioteca.api.dto.response.UsuarioResponse;
import com.biblioteca.api.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@PostMapping
	public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody CriarUsuarioRequest request) {
		UsuarioResponse response = usuarioService.criar(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		return usuarioService.login(request);
	}

	@GetMapping("/{id}")
	public UsuarioResponse buscarPorId(@PathVariable("id") UUID id) {
		return usuarioService.buscarPorId(id);
	}

	@PutMapping("/{id}")
	public UsuarioResponse atualizar(
			@PathVariable("id") UUID id,
			@Valid @RequestBody AtualizarUsuarioRequest request) {
		return usuarioService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable("id") UUID id) {
		usuarioService.excluir(id);
		return ResponseEntity.noContent().build();
	}
}
