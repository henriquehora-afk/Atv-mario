package com.biblioteca.api.model;

import java.util.List;

/** Critérios de busca. Campos nulos não filtram. */
public record FiltroObras(List<String> termos, String tipo, Integer anoDe, Integer anoAte, int limite) {

	public FiltroObras {
		termos = termos == null ? List.of() : List.copyOf(termos);
	}
}