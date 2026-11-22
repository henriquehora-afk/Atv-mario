package com.biblioteca.api.model;

import java.time.Instant;

public class Usuario {

	private final Long id;
	private final String nome;
	private final String email;
	private final String senhaHash;
	private final Instant criadoEm;
	private final Instant atualizadoEm;

	public Usuario(
			Long id,
			String nome,
			String email,
			String senhaHash,
			Instant criadoEm,
			Instant atualizadoEm) {
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.senhaHash = senhaHash;
		this.criadoEm = criadoEm;
		this.atualizadoEm = atualizadoEm;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getEmail() {
		return email;
	}

	public String getSenhaHash() {
		return senhaHash;
	}

	public Instant getCriadoEm() {
		return criadoEm;
	}

	public Instant getAtualizadoEm() {
		return atualizadoEm;
	}
}