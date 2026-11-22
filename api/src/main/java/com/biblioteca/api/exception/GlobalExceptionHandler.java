package com.biblioteca.api.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.biblioteca.api.dto.response.ApiErrorResponse;
import com.biblioteca.api.pdf.PdfIndisponivelException;
import com.biblioteca.api.pdf.UrlInvalidaException;
import com.biblioteca.api.security.NaoAutenticadoException;

/** Converte exceções em respostas JSON. O front exibe o campo "message". */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler({ CredenciaisInvalidasException.class, NaoAutenticadoException.class })
	public ResponseEntity<ApiErrorResponse> handleNaoAutorizado(RuntimeException exception) {
		return error(HttpStatus.UNAUTHORIZED, exception.getMessage(), Map.of());
	}

	@ExceptionHandler({
			UsuarioNaoEncontradoException.class,
			ObraNaoEncontradaException.class,
			ObraSemPdfException.class })
	public ResponseEntity<ApiErrorResponse> handleNaoEncontrado(RuntimeException exception) {
		return error(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
	}

	@ExceptionHandler(EmailJaCadastradoException.class)
	public ResponseEntity<ApiErrorResponse> handleEmailJaCadastrado(EmailJaCadastradoException exception) {
		return error(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
	}

	@ExceptionHandler({ SenhaAtualIncorretaException.class, UrlInvalidaException.class })
	public ResponseEntity<ApiErrorResponse> handleRequisicaoInvalida(RuntimeException exception) {
		return error(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
	}

	@ExceptionHandler(PdfIndisponivelException.class)
	public ResponseEntity<ApiErrorResponse> handlePdfIndisponivel(PdfIndisponivelException exception) {
		log.warn("PDF indisponível: {}", exception.getMessage());
		return error(HttpStatus.BAD_GATEWAY, exception.getMessage(), Map.of());
	}

	@ExceptionHandler(FuncionalidadeIndisponivelException.class)
	public ResponseEntity<ApiErrorResponse> handleIndisponivel(FuncionalidadeIndisponivelException exception) {
		return error(HttpStatus.NOT_IMPLEMENTED, exception.getMessage(), Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidacao(MethodArgumentNotValidException exception) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors().forEach(fieldError ->
				fieldErrors.putIfAbsent(
						fieldError.getField(),
						fieldError.getDefaultMessage() == null ? "Valor inválido." : fieldError.getDefaultMessage()));
		// A primeira mensagem vai no "message" para o front mostrar algo útil.
		String mensagem = fieldErrors.isEmpty() ? "Dados inválidos." : fieldErrors.values().iterator().next();
		return error(HttpStatus.BAD_REQUEST, mensagem, fieldErrors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleCorpoInvalido(HttpMessageNotReadableException exception) {
		return error(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou JSON malformado.", Map.of());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleParametroInvalido(MethodArgumentTypeMismatchException exception) {
		return error(HttpStatus.BAD_REQUEST, "Parâmetro inválido: " + exception.getName() + ".", Map.of());
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ApiErrorResponse> handleBancoDeDados(DataAccessException exception) {
		log.error("Falha ao acessar o banco de dados.", exception);
		return error(
				HttpStatus.SERVICE_UNAVAILABLE,
				"Serviço temporariamente indisponível. Tente novamente em instantes.",
				Map.of());
	}

	private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message, Map<String, String> fieldErrors) {
		return ResponseEntity.status(status).body(new ApiErrorResponse(
				Instant.now(), status.value(), status.getReasonPhrase(), message, fieldErrors));
	}
}