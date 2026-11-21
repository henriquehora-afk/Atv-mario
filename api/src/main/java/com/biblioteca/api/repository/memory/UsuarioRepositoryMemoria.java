package com.biblioteca.api.repository.memory;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Repository;

import com.biblioteca.api.model.Usuario;
import com.biblioteca.api.repository.UsuarioRepository;

@Repository
public class UsuarioRepositoryMemoria implements UsuarioRepository {

	private final ConcurrentMap<UUID, Usuario> usuarios = new ConcurrentHashMap<>();

	@Override
	public synchronized boolean saveIfEmailUnique(Usuario usuario) {
		if (usuarios.containsKey(usuario.getId()) || emailUsedByAnotherUser(usuario)) {
			return false;
		}

		usuarios.put(usuario.getId(), usuario);
		return true;
	}

	@Override
	public synchronized boolean updateIfEmailUnique(Usuario usuario) {
		if (!usuarios.containsKey(usuario.getId()) || emailUsedByAnotherUser(usuario)) {
			return false;
		}

		usuarios.put(usuario.getId(), usuario);
		return true;
	}

	@Override
	public Optional<Usuario> findById(UUID id) {
		return Optional.ofNullable(usuarios.get(id));
	}

	@Override
	public Optional<Usuario> findByEmail(String email) {
		return usuarios.values().stream()
				.filter(usuario -> usuario.getEmail().equals(email))
				.findFirst();
	}

	@Override
	public boolean existsByEmail(String email) {
		return findByEmail(email).isPresent();
	}

	@Override
	public synchronized boolean deleteById(UUID id) {
		return usuarios.remove(id) != null;
	}

	private boolean emailUsedByAnotherUser(Usuario usuario) {
		return usuarios.values().stream()
				.anyMatch(existing -> existing.getEmail().equals(usuario.getEmail())
						&& !existing.getId().equals(usuario.getId()));
	}
}
