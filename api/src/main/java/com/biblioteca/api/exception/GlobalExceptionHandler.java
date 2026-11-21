package com.biblioteca.api.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.biblioteca.api.dto.response.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CredenciaisInvalidasException.class)
	public ResponseEntity<ApiErrorResponse> handleCredenciaisInvalidas(
			CredenciaisInvalidasException exception) {
		return error(HttpStatus.UNAUTHORIZED, exception.getMessage(), Map.of());
	}

	@ExceptionHandler(UsuarioNaoEncontradoException.class)
	public ResponseEntity<ApiErrorResponse> handleUsuarioNaoEncontrado(
			UsuarioNaoEncontradoException exception) {
		return error(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
	}

	@ExceptionHandler(EmailJaCadastradoException.class)
	public ResponseEntity<ApiErrorResponse> handleEmailJaCadastrado(
			EmailJaCadastradoException exception) {
		return error(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidacao(
			MethodArgumentNotValidException exception) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors().forEach(fieldError ->
				fieldErrors.putIfAbsent(
						fieldError.getField(),
						fieldError.getDefaultMessage() == null
								? "Valor inválido."
								: fieldError.getDefaultMessage()));
		return error(HttpStatus.BAD_REQUEST, "Dados inválidos.", fieldErrors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleCorpoInvalido(
			HttpMessageNotReadableException exception) {
		return error(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou JSON malformado.", Map.of());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleParametroInvalido(
			MethodArgumentTypeMismatchException exception) {
		return error(
				HttpStatus.BAD_REQUEST,
				"Parâmetro inválido: " + exception.getName() + ".",
				Map.of());
	}

	private ResponseEntity<ApiErrorResponse> error(
			HttpStatus status,
			String message,
			Map<String, String> fieldErrors) {
		ApiErrorResponse response = new ApiErrorResponse(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				fieldErrors);
		return ResponseEntity.status(status).body(response);
	}
}
