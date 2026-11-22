package com.biblioteca.api.model;

import java.time.Instant;
import java.util.List;

/** Obra acadêmica (artigo, livro ou tese). O PDF é lido a partir de pdfUrl. */
public record Obra(
		Long id,
		String fonte,
		String titulo,
		List<String> autores,
		Integer ano,
		String tipo,
		String resumo,
		String doi,
		String url,
		List<String> palavrasChave,
		String pdfUrl,
		Long criadoPor,
		Instant criadoEm) {

	public Obra {
		autores = autores == null ? List.of() : List.copyOf(autores);
		palavrasChave = palavrasChave == null ? List.of() : List.copyOf(palavrasChave);
	}

	public boolean temPdf() {
		return pdfUrl != null && !pdfUrl.isBlank();
	}
}