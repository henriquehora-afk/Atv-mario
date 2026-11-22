package com.biblioteca.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.api.dto.response.VisitaResponse;
import com.biblioteca.api.security.UsuarioLogado;
import com.biblioteca.api.service.HistoricoService;

/** Histórico de obras visitadas. Cada usuário só vê e altera o próprio histórico. */
@RestController
@RequestMapping("/api/v1/users/me/historico")
public class HistoricoController {

	private final HistoricoService historicoService;

	public HistoricoController(HistoricoService historicoService) {
		this.historicoService = historicoService;
	}

	@GetMapping
	public List<VisitaResponse> listar(@UsuarioLogado Long usuarioId) {
		return historicoService.listar(usuarioId);
	}

	@PostMapping("/{obraId}")
	public ResponseEntity<Void> registrar(@UsuarioLogado Long usuarioId, @PathVariable("obraId") Long obraId) {
		historicoService.registrarVisita(usuarioId, obraId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{obraId}")
	public ResponseEntity<Void> remover(@UsuarioLogado Long usuarioId, @PathVariable("obraId") Long obraId) {
		historicoService.remover(usuarioId, obraId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> limpar(@UsuarioLogado Long usuarioId) {
		historicoService.limpar(usuarioId);
		return ResponseEntity.noContent().build();
	}
}