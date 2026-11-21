package com.biblioteca.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AtualizarUsuarioRequest(
		@NotBlank(message = "Nome é obrigatório.") String nome,
		@NotBlank(message = "E-mail é obrigatório.")
		@Email(message = "E-mail deve ter um formato válido.") String email) {

	public AtualizarUsuarioRequest {
		if (email != null) {
			email = email.trim();
		}
	}
}
