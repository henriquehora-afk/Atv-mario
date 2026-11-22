package com.biblioteca.api.repository.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.biblioteca.api.model.Usuario;
import com.biblioteca.api.repository.UsuarioRepository;

/**
 * Implementação de UsuarioRepository que grava no PostgreSQL.
 * O id é gerado pelo banco (IDENTITY): 1, 2, 3...
 */
@Repository
public class UsuarioRepositoryPostgres implements UsuarioRepository {

	private static final String COLUNAS = "id, nome, email, senha_hash, criado_em, atualizado_em";

	private final JdbcClient jdbc;

	public UsuarioRepositoryPostgres(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public Optional<Usuario> saveIfEmailUnique(Usuario usuario) {
		// O banco gera o id e o devolve com RETURNING.
		// Se o e-mail já existir, nada é inserido e não volta nenhum id.
		return jdbc.sql("""
				INSERT INTO usuarios (nome, email, senha_hash, criado_em, atualizado_em)
				VALUES (:nome, :email, :senhaHash, :criadoEm, :atualizadoEm)
				ON CONFLICT (email) DO NOTHING
				RETURNING id
				""")
				.param("nome", usuario.getNome())
				.param("email", usuario.getEmail())
				.param("senhaHash", usuario.getSenhaHash())
				.param("criadoEm", paraBanco(usuario.getCriadoEm()))
				.param("atualizadoEm", paraBanco(usuario.getAtualizadoEm()))
				.query(Long.class)
				.optional()
				.map(idGerado -> new Usuario(
						idGerado,
						usuario.getNome(),
						usuario.getEmail(),
						usuario.getSenhaHash(),
						usuario.getCriadoEm(),
						usuario.getAtualizadoEm()));
	}

	@Override
	public boolean updateIfEmailUnique(Usuario usuario) {
		try {
			int linhas = jdbc.sql("""
					UPDATE usuarios
					   SET nome = :nome,
					       email = :email,
					       senha_hash = :senhaHash,
					       atualizado_em = :atualizadoEm
					 WHERE id = :id
					""")
					.param("id", usuario.getId())
					.param("nome", usuario.getNome())
					.param("email", usuario.getEmail())
					.param("senhaHash", usuario.getSenhaHash())
					.param("atualizadoEm", paraBanco(usuario.getAtualizadoEm()))
					.update();
			return linhas == 1;
		} catch (DuplicateKeyException emailEmUso) {
			// Outro usuário já usa este e-mail
			return false;
		}
	}

	@Override
	public Optional<Usuario> findById(Long id) {
		return jdbc.sql("SELECT " + COLUNAS + " FROM usuarios WHERE id = :id")
				.param("id", id)
				.query(UsuarioRepositoryPostgres::mapear)
				.optional();
	}

	@Override
	public Optional<Usuario> findByEmail(String email) {
		return jdbc.sql("SELECT " + COLUNAS + " FROM usuarios WHERE email = :email")
				.param("email", email)
				.query(UsuarioRepositoryPostgres::mapear)
				.optional();
	}

	@Override
	public boolean existsByEmail(String email) {
		return Boolean.TRUE.equals(jdbc.sql("SELECT EXISTS (SELECT 1 FROM usuarios WHERE email = :email)")
				.param("email", email)
				.query(Boolean.class)
				.single());
	}

	@Override
	public boolean deleteById(Long id) {
		return jdbc.sql("DELETE FROM usuarios WHERE id = :id")
				.param("id", id)
				.update() == 1;
	}

	// Converte uma linha da tabela em objeto Usuario
	private static Usuario mapear(ResultSet rs, int numeroLinha) throws SQLException {
		return new Usuario(
				rs.getLong("id"),
				rs.getString("nome"),
				rs.getString("email"),
				rs.getString("senha_hash"),
				rs.getObject("criado_em", OffsetDateTime.class).toInstant(),
				rs.getObject("atualizado_em", OffsetDateTime.class).toInstant());
	}

	private static OffsetDateTime paraBanco(Instant instante) {
		return instante.atOffset(ZoneOffset.UTC);
	}
}