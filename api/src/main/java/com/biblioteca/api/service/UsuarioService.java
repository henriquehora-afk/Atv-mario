package com.biblioteca.api.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.biblioteca.api.dto.request.ChangePasswordRequest;
import com.biblioteca.api.dto.request.LoginRequest;
import com.biblioteca.api.dto.request.RegisterRequest;
import com.biblioteca.api.dto.request.UpdateMeRequest;
import com.biblioteca.api.dto.response.UserResponse;
import com.biblioteca.api.exception.CredenciaisInvalidasException;
import com.biblioteca.api.exception.EmailJaCadastradoException;
import com.biblioteca.api.exception.SenhaAtualIncorretaException;
import com.biblioteca.api.exception.UsuarioNaoEncontradoException;
import com.biblioteca.api.model.Usuario;
import com.biblioteca.api.repository.UsuarioRepository;

/** Regras de usuário. Depende só das abstrações UsuarioRepository e PasswordEncoder. */
@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserResponse registrar(RegisterRequest request) {
		Instant agora = agora();
		Usuario novo = new Usuario(
				null, // gerado pelo banco
				request.name(),
				normalizarEmail(request.email()),
				passwordEncoder.encode(request.password()),
				agora,
				agora);
		return paraResponse(usuarioRepository.saveIfEmailUnique(novo)
				.orElseThrow(EmailJaCadastradoException::new));
	}

	public UserResponse autenticar(LoginRequest request) {
		Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(request.email()))
				.orElseThrow(CredenciaisInvalidasException::new);
		if (!passwordEncoder.matches(request.password(), usuario.getSenhaHash())) {
			throw new CredenciaisInvalidasException();
		}
		return paraResponse(usuario);
	}

	public UserResponse buscarPorId(Long id) {
		return paraResponse(buscar(id));
	}

	public UserResponse atualizar(Long id, UpdateMeRequest request) {
		Usuario atual = buscar(id);
		Usuario atualizado = new Usuario(
				atual.getId(),
				request.name(),
				normalizarEmail(request.email()),
				atual.getSenhaHash(),
				atual.getCriadoEm(),
				agora());
		salvarAlteracao(atualizado);
		return paraResponse(atualizado);
	}

	public void alterarSenha(Long id, ChangePasswordRequest request) {
		Usuario atual = buscar(id);
		if (!passwordEncoder.matches(request.currentPassword(), atual.getSenhaHash())) {
			throw new SenhaAtualIncorretaException();
		}
		salvarAlteracao(new Usuario(
				atual.getId(),
				atual.getNome(),
				atual.getEmail(),
				passwordEncoder.encode(request.newPassword()),
				atual.getCriadoEm(),
				agora()));
	}

	private void salvarAlteracao(Usuario usuario) {
		if (!usuarioRepository.updateIfEmailUnique(usuario)) {
			if (usuarioRepository.findById(usuario.getId()).isEmpty()) {
				throw new UsuarioNaoEncontradoException();
			}
			throw new EmailJaCadastradoException();
		}
	}

	private Usuario buscar(Long id) {
		return usuarioRepository.findById(id).orElseThrow(UsuarioNaoEncontradoException::new);
	}

	private Instant agora() {
		return Instant.now().truncatedTo(ChronoUnit.MICROS);
	}

	private String normalizarEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private UserResponse paraResponse(Usuario usuario) {
		return new UserResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getCriadoEm());
	}
}