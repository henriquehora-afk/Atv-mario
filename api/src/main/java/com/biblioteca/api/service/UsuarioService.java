package com.biblioteca.api.service;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.biblioteca.api.dto.request.AtualizarUsuarioRequest;
import com.biblioteca.api.dto.request.CriarUsuarioRequest;
import com.biblioteca.api.dto.request.LoginRequest;
import com.biblioteca.api.dto.response.LoginResponse;
import com.biblioteca.api.dto.response.UsuarioResponse;
import com.biblioteca.api.exception.CredenciaisInvalidasException;
import com.biblioteca.api.exception.EmailJaCadastradoException;
import com.biblioteca.api.exception.UsuarioNaoEncontradoException;
import com.biblioteca.api.model.Usuario;
import com.biblioteca.api.repository.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UsuarioResponse criar(CriarUsuarioRequest request) {
		Instant agora = Instant.now();
		Usuario usuario = new Usuario(
				UUID.randomUUID(),
				request.nome(),
				normalizarEmail(request.email()),
				passwordEncoder.encode(request.senha()),
				agora,
				agora);

		if (!usuarioRepository.saveIfEmailUnique(usuario)) {
			throw new EmailJaCadastradoException();
		}

		return paraResponse(usuario);
	}

	public LoginResponse login(LoginRequest request) {
		Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(request.email()))
				.orElseThrow(CredenciaisInvalidasException::new);
		if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
			throw new CredenciaisInvalidasException();
		}

		return new LoginResponse(paraResponse(usuario));
	}

	public UsuarioResponse atualizar(UUID id, AtualizarUsuarioRequest request) {
		Usuario atual = usuarioRepository.findById(id)
				.orElseThrow(UsuarioNaoEncontradoException::new);
		Usuario atualizado = new Usuario(
				atual.getId(),
				request.nome(),
				normalizarEmail(request.email()),
				atual.getSenhaHash(),
				atual.getCriadoEm(),
				Instant.now());

		if (!usuarioRepository.updateIfEmailUnique(atualizado)) {
			if (usuarioRepository.findById(id).isEmpty()) {
				throw new UsuarioNaoEncontradoException();
			}
			throw new EmailJaCadastradoException();
		}

		return paraResponse(atualizado);
	}

	public UsuarioResponse buscarPorId(UUID id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(UsuarioNaoEncontradoException::new);
		return paraResponse(usuario);
	}

	public void excluir(UUID id) {
		if (!usuarioRepository.deleteById(id)) {
			throw new UsuarioNaoEncontradoException();
		}
	}

	private String normalizarEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private UsuarioResponse paraResponse(Usuario usuario) {
		return new UsuarioResponse(
				usuario.getId(),
				usuario.getNome(),
				usuario.getEmail(),
				usuario.getCriadoEm(),
				usuario.getAtualizadoEm());
	}
}
