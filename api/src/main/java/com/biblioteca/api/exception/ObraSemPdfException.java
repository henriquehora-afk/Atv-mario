package com.biblioteca.api.exception;

public class ObraSemPdfException extends RuntimeException {

	public ObraSemPdfException() {
		super("Esta obra não tem um PDF disponível.");
	}
}