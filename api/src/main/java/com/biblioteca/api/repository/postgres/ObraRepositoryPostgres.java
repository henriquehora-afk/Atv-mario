package com.biblioteca.api.repository.postgres;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.biblioteca.api.model.FiltroObras;
import com.biblioteca.api.model.Obra;
import com.biblioteca.api.repository.ObraRepository;

@Repository
public class ObraRepositoryPostgres implements ObraRepository {

	static final String COLUNAS = """
			o.id, o.fonte, o.titulo, o.autores, o.ano, o.tipo, o.resumo, o.doi, o.url,
			o.palavras_chave, o.pdf_url, o.criado_por, o.criado_em""";

	// Listas (autores, palavras-chave) vão ao banco como texto separado por este
	// caractere de controle e viram TEXT[] com string_to_array no SQL.
	private static final String SEPARADOR = "\u001F";

	private final JdbcClient jdbc;

	public ObraRepositoryPostgres(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public Obra salvar(Obra obra) {
		return jdbc.sql("""
				INSERT INTO obras (fonte, titulo, autores, ano, tipo, resumo, doi, url, palavras_chave, pdf_url, criado_por)
				VALUES (:fonte, :titulo, string_to_array(:autores, chr(31)), :ano, :tipo, :resumo, :doi, :url,
				        string_to_array(:palavrasChave, chr(31)), :pdfUrl, :criadoPor)
				RETURNING id, fonte, titulo, autores, ano, tipo, resumo, doi, url,
				          palavras_chave, pdf_url, criado_por, criado_em
				""")
				.param("fonte", obra.fonte())
				.param("titulo", obra.titulo())
				.param("autores", juntar(obra.autores()))
				.param("ano", obra.ano())
				.param("tipo", obra.tipo())
				.param("resumo", obra.resumo())
				.param("doi", obra.doi())
				.param("url", obra.url())
				.param("palavrasChave", juntar(obra.palavrasChave()))
				.param("pdfUrl", obra.pdfUrl())
				.param("criadoPor", obra.criadoPor())
				.query(ObraRepositoryPostgres::mapear)
				.single();
	}

	@Override
	public Optional<Obra> buscarPorId(Long id) {
		return jdbc.sql("SELECT " + COLUNAS + " FROM obras o WHERE o.id = :id")
				.param("id", id)
				.query(ObraRepositoryPostgres::mapear)
				.optional();
	}

	@Override
	public List<Obra> pesquisar(FiltroObras filtro) {
		StringBuilder sql = new StringBuilder("SELECT " + COLUNAS + " FROM obras o WHERE TRUE");
		Map<String, Object> params = new LinkedHashMap<>();

		// Cada termo precisa aparecer no título, autores, resumo ou palavras-chave.
		for (int i = 0; i < filtro.termos().size(); i++) {
			sql.append("""
					 AND (o.titulo || ' ' || array_to_string(o.autores, ' ') || ' ' ||
					      coalesce(o.resumo, '') || ' ' || array_to_string(o.palavras_chave, ' ')) ILIKE :t%d ESCAPE '\\'
					""".formatted(i));
			params.put("t" + i, "%" + escaparLike(filtro.termos().get(i)) + "%");
		}
		if (filtro.tipo() != null) {
			sql.append(" AND o.tipo = :tipo");
			params.put("tipo", filtro.tipo());
		}
		if (filtro.anoDe() != null) {
			sql.append(" AND o.ano >= :anoDe");
			params.put("anoDe", filtro.anoDe());
		}
		if (filtro.anoAte() != null) {
			sql.append(" AND o.ano <= :anoAte");
			params.put("anoAte", filtro.anoAte());
		}
		sql.append(" ORDER BY o.ano DESC, o.id DESC LIMIT :limite");
		params.put("limite", filtro.limite());

		return jdbc.sql(sql.toString())
				.params(params)
				.query(ObraRepositoryPostgres::mapear)
				.list();
	}

	static Obra mapear(ResultSet rs, int numeroLinha) throws SQLException {
		return new Obra(
				rs.getLong("id"),
				rs.getString("fonte"),
				rs.getString("titulo"),
				lista(rs.getArray("autores")),
				rs.getInt("ano"),
				rs.getString("tipo"),
				rs.getString("resumo"),
				rs.getString("doi"),
				rs.getString("url"),
				lista(rs.getArray("palavras_chave")),
				rs.getString("pdf_url"),
				rs.getObject("criado_por", Long.class),
				rs.getObject("criado_em", OffsetDateTime.class).toInstant());
	}

	private static List<String> lista(Array array) throws SQLException {
		if (array == null) {
			return List.of();
		}
		return Arrays.asList((String[]) array.getArray());
	}

	private static String juntar(List<String> valores) {
		return String.join(SEPARADOR, valores.stream().map(v -> v.replace(SEPARADOR, "")).toList());
	}

	private static String escaparLike(String termo) {
		return termo.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	}
}