package com.biblioteca.api.dto.response;

import java.time.Instant;

public record UsuarioResponse(
		Long id,
		String nome,
		String email,
		Instant criadoEm,
		Instant atualizadoEm) {
}