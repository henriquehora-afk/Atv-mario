package com.biblioteca.api.config;

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;

import com.biblioteca.api.security.UsuarioLogado;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(
		info = @Info(title = "Biblioteca Acadêmica API", version = "v1"),
		security = @SecurityRequirement(name = "bearer"))
@SecurityScheme(name = "bearer", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

	static {
		// O id do usuário vem do token, não de um campo da requisição:
		// não mostrar @UsuarioLogado como parâmetro no Swagger.
		SpringDocUtils.getConfig().addAnnotationsToIgnore(UsuarioLogado.class);
	}
}