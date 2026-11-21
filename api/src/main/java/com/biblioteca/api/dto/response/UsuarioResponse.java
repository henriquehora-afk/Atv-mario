package com.biblioteca.api.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UsuarioResponse(
		UUID id,
		String nome,
		String email,
		Instant criadoEm,
		Instant atualizadoEm) {
}
