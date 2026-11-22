package com.biblioteca.api.exception;

public class SenhaAtualIncorretaException extends RuntimeException {

	public SenhaAtualIncorretaException() {
		super("A senha atual está incorreta.");
	}
}