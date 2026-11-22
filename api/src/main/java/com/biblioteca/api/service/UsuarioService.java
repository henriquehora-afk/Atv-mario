package com.biblioteca.api.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
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
	private final PasswordEncoder passwordEncoder; // interface, não a classe BCrypt

	public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UsuarioResponse criar(CriarUsuarioRequest request) {
		Instant agora = agora();
		Usuario novo = new Usuario(
				null, // o id é gerado pelo banco (1, 2, 3...)
				request.nome(),
				normalizarEmail(request.email()),
				passwordEncoder.encode(request.senha()),
				agora,
				agora);

		// O repositório devolve o usuário já com o id gerado.
		// Se vier vazio, o e-mail já estava cadastrado.
		Usuario salvo = usuarioRepository.saveIfEmailUnique(novo)
				.orElseThrow(EmailJaCadastradoException::new);

		return paraResponse(salvo);
	}

	public LoginResponse login(LoginRequest request) {
		Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(request.email()))
				.orElseThrow(CredenciaisInvalidasException::new);
		if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
			throw new CredenciaisInvalidasException();
		}

		return new LoginResponse(paraResponse(usuario));
	}

	public UsuarioResponse atualizar(Long id, AtualizarUsuarioRequest request) {
		Usuario atual = usuarioRepository.findById(id)
				.orElseThrow(UsuarioNaoEncontradoException::new);
		Usuario atualizado = new Usuario(
				atual.getId(),
				request.nome(),
				normalizarEmail(request.email()),
				atual.getSenhaHash(),
				atual.getCriadoEm(),
				agora());

		if (!usuarioRepository.updateIfEmailUnique(atualizado)) {
			if (usuarioRepository.findById(id).isEmpty()) {
				throw new UsuarioNaoEncontradoException();
			}
			throw new EmailJaCadastradoException();
		}

		return paraResponse(atualizado);
	}

	public UsuarioResponse buscarPorId(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(UsuarioNaoEncontradoException::new);
		return paraResponse(usuario);
	}

	public void excluir(Long id) {
		if (!usuarioRepository.deleteById(id)) {
			throw new UsuarioNaoEncontradoException();
		}
	}

	// Microssegundos: mesma precisão do TIMESTAMPTZ do PostgreSQL
	private Instant agora() {
		return Instant.now().truncatedTo(ChronoUnit.MICROS);
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