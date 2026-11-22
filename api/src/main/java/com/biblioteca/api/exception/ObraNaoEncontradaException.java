package com.biblioteca.api.exception;

public class ObraNaoEncontradaException extends RuntimeException {

	public ObraNaoEncontradaException() {
		super("Obra não encontrada.");
	}
}