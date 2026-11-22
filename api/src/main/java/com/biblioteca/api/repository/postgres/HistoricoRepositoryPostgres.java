package com.biblioteca.api.repository.postgres;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.biblioteca.api.model.VisitaHistorico;
import com.biblioteca.api.repository.HistoricoRepository;

@Repository
public class HistoricoRepositoryPostgres implements HistoricoRepository {

	private final JdbcClient jdbc;

	public HistoricoRepositoryPostgres(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public void registrarVisita(Long usuarioId, Long obraId, Instant visitadoEm) {
		jdbc.sql("""
				INSERT INTO historico_visitas (usuario_id, obra_id, visitado_em)
				VALUES (:usuarioId, :obraId, :visitadoEm)
				ON CONFLICT (usuario_id, obra_id) DO UPDATE SET visitado_em = EXCLUDED.visitado_em
				""")
				.param("usuarioId", usuarioId)
				.param("obraId", obraId)
				.param("visitadoEm", visitadoEm.atOffset(ZoneOffset.UTC))
				.update();
	}

	@Override
	public List<VisitaHistorico> listar(Long usuarioId, int limite) {
		return jdbc.sql("SELECT " + ObraRepositoryPostgres.COLUNAS + """
				, h.visitado_em
				  FROM historico_visitas h
				  JOIN obras o ON o.id = h.obra_id
				 WHERE h.usuario_id = :usuarioId
				 ORDER BY h.visitado_em DESC
				 LIMIT :limite
				""")
				.param("usuarioId", usuarioId)
				.param("limite", limite)
				.query((rs, linha) -> new VisitaHistorico(
						ObraRepositoryPostgres.mapear(rs, linha),
						rs.getObject("visitado_em", OffsetDateTime.class).toInstant()))
				.list();
	}

	@Override
	public boolean remover(Long usuarioId, Long obraId) {
		return jdbc.sql("DELETE FROM historico_visitas WHERE usuario_id = :usuarioId AND obra_id = :obraId")
				.param("usuarioId", usuarioId)
				.param("obraId", obraId)
				.update() == 1;
	}

	@Override
	public void limpar(Long usuarioId) {
		jdbc.sql("DELETE FROM historico_visitas WHERE usuario_id = :usuarioId")
				.param("usuarioId", usuarioId)
				.update();
	}
}