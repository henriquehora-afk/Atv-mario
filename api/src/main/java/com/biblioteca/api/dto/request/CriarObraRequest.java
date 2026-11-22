package com.biblioteca.api.dto.request;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Cadastro de obra a partir do link de um PDF. */
public record CriarObraRequest(
		@NotBlank(message = "Título é obrigatório.")
		@Size(max = 300, message = "Título deve ter no máximo 300 caracteres.") String titulo,
		@NotEmpty(message = "Informe ao menos um autor.")
		@Size(max = 30, message = "Informe no máximo 30 autores.")
		List<@NotBlank(message = "Nome de autor vazio.") @Size(max = 150, message = "Nome de autor muito longo.") String> autores,
		@NotNull(message = "Ano é obrigatório.")
		@Min(value = 1000, message = "Ano inválido.")
		@Max(value = 2100, message = "Ano inválido.") Integer ano,
		@NotBlank(message = "Tipo é obrigatório.")
		@Pattern(regexp = "Artigo|Livro|Tese", message = "Tipo deve ser Artigo, Livro ou Tese.") String tipo,
		@Size(max = 5000, message = "Resumo deve ter no máximo 5000 caracteres.") String resumo,
		@Size(max = 100, message = "DOI deve ter no máximo 100 caracteres.") String doi,
		@Size(max = 2000, message = "Link da página deve ter no máximo 2000 caracteres.")
		@Pattern(regexp = "^$|^https?://.+", message = "O link da página precisa começar com http:// ou https://.") String url,
		@Size(max = 20, message = "Informe no máximo 20 palavras-chave.")
		List<@NotBlank(message = "Palavra-chave vazia.") @Size(max = 60, message = "Palavra-chave muito longa.") String> palavrasChave,
		@NotBlank(message = "O link do PDF é obrigatório.")
		@Size(max = 2000, message = "Link do PDF deve ter no máximo 2000 caracteres.")
		@Pattern(regexp = "^https?://.+", message = "O link do PDF precisa começar com http:// ou https://.") String pdfUrl) {
}