package com.biblioteca.api.pdf;

public class UrlInvalidaException extends RuntimeException {

	public UrlInvalidaException(String mensagem) {
		super(mensagem);
	}
}