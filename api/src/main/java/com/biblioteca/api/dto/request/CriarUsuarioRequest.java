package com.biblioteca.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CriarUsuarioRequest(
		@NotBlank(message = "Nome é obrigatório.") String nome,
		@NotBlank(message = "E-mail é obrigatório.")
		@Email(message = "E-mail deve ter um formato válido.") String email,
		@NotBlank(message = "Senha é obrigatória.") String senha) {

	public CriarUsuarioRequest {
		if (email != null) {
			email = email.trim();
		}
	}
}
