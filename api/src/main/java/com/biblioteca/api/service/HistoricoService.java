package com.biblioteca.api.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.biblioteca.api.dto.response.ObraResponse;
import com.biblioteca.api.dto.response.VisitaResponse;
import com.biblioteca.api.exception.ObraNaoEncontradaException;
import com.biblioteca.api.repository.HistoricoRepository;
import com.biblioteca.api.repository.ObraRepository;

/** Histórico de obras visitadas pelo usuário logado. */
@Service
public class HistoricoService {

	private static final int LIMITE = 200;

	private final HistoricoRepository historicoRepository;
	private final ObraRepository obraRepository;

	public HistoricoService(HistoricoRepository historicoRepository, ObraRepository obraRepository) {
		this.historicoRepository = historicoRepository;
		this.obraRepository = obraRepository;
	}

	public void registrarVisita(Long usuarioId, Long obraId) {
		if (obraRepository.buscarPorId(obraId).isEmpty()) {
			throw new ObraNaoEncontradaException();
		}
		historicoRepository.registrarVisita(usuarioId, obraId, Instant.now().truncatedTo(ChronoUnit.MICROS));
	}

	public List<VisitaResponse> listar(Long usuarioId) {
		return historicoRepository.listar(usuarioId, LIMITE).stream()
				.map(v -> new VisitaResponse(ObraResponse.de(v.obra()), v.visitadoEm()))
				.toList();
	}

	public void remover(Long usuarioId, Long obraId) {
		historicoRepository.remover(usuarioId, obraId);
	}

	public void limpar(Long usuarioId) {
		historicoRepository.limpar(usuarioId);
	}
}