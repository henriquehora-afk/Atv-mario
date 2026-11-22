package com.biblioteca.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMeRequest(
		@NotBlank(message = "Nome é obrigatório.")
		@Size(max = 150, message = "Nome deve ter no máximo 150 caracteres.") String name,
		@NotBlank(message = "E-mail é obrigatório.")
		@Email(message = "E-mail deve ter um formato válido.")
		@Size(max = 254, message = "E-mail deve ter no máximo 254 caracteres.") String email) {

	public UpdateMeRequest {
		if (email != null) {
			email = email.trim();
		}
		if (name != null) {
			name = name.trim();
		}
	}
}