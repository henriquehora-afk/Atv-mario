package com.biblioteca.api.exception;

public class FuncionalidadeIndisponivelException extends RuntimeException {

	public FuncionalidadeIndisponivelException(String mensagem) {
		super(mensagem);
	}
}