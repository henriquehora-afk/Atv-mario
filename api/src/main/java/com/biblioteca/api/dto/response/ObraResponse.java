package com.biblioteca.api.dto.response;

import java.util.List;

import com.biblioteca.api.model.Obra;

public record ObraResponse(
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
		boolean temPdf) {

	public static ObraResponse de(Obra obra) {
		return new ObraResponse(
				obra.id(), obra.fonte(), obra.titulo(), obra.autores(), obra.ano(), obra.tipo(),
				obra.resumo(), obra.doi(), obra.url(), obra.palavrasChave(), obra.temPdf());
	}
}