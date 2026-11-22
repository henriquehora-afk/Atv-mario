package com.biblioteca.api.repository;

import java.time.Instant;
import java.util.List;

import com.biblioteca.api.model.VisitaHistorico;

/** Abstração do histórico de obras visitadas por usuário. */
public interface HistoricoRepository {

	/** Registra a visita; se a obra já estava no histórico, só atualiza a data. */
	void registrarVisita(Long usuarioId, Long obraId, Instant visitadoEm);

	/** Visitas do usuário, da mais recente para a mais antiga. */
	List<VisitaHistorico> listar(Long usuarioId, int limite);

	boolean remover(Long usuarioId, Long obraId);

	void limpar(Long usuarioId);
}