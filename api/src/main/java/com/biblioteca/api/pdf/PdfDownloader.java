package com.biblioteca.api.pdf;

/** Abstração para obter os bytes de um PDF a partir de um link. */
public interface PdfDownloader {

	/**
	 * @throws UrlInvalidaException se o link não for permitido
	 * @throws PdfIndisponivelException se o arquivo não puder ser obtido ou não for PDF
	 */
	byte[] baixar(String url);
}