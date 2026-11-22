package com.biblioteca.api.security;

public class NaoAutenticadoException extends RuntimeException {

	public NaoAutenticadoException() {
		super("Sessão expirada ou inválida. Entre novamente.");
	}
}