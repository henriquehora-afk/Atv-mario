package com.biblioteca.api.repository;

import java.util.Optional;
import java.util.UUID;

import com.biblioteca.api.model.Usuario;

public interface UsuarioRepository {

	boolean saveIfEmailUnique(Usuario usuario);

	boolean updateIfEmailUnique(Usuario usuario);

	Optional<Usuario> findById(UUID id);

	Optional<Usuario> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean deleteById(UUID id);
}
