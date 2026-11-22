package com.biblioteca.api.repository;

import java.util.List;
import java.util.Optional;

import com.biblioteca.api.model.FiltroObras;
import com.biblioteca.api.model.Obra;

/** Abstração de persistência de obras. */
public interface ObraRepository {

	/** Salva e devolve a obra com id e data gerados pelo banco. */
	Obra salvar(Obra obra);

	Optional<Obra> buscarPorId(Long id);

	List<Obra> pesquisar(FiltroObras filtro);
}