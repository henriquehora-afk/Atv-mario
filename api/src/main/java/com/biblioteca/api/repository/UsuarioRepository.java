package com.biblioteca.api.repository;

import java.util.Optional;

import com.biblioteca.api.model.Usuario;

public interface UsuarioRepository {

	/** Salva e devolve o usuário com o id gerado pelo banco. Vazio se o e-mail já existir. */
	Optional<Usuario> saveIfEmailUnique(Usuario usuario);

	boolean updateIfEmailUnique(Usuario usuario);

	Optional<Usuario> findById(Long id);

	Optional<Usuario> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean deleteById(Long id);
}