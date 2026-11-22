package com.biblioteca.api.pdf;

public class PdfIndisponivelException extends RuntimeException {

	public PdfIndisponivelException(String mensagem) {
		super(mensagem);
	}
}