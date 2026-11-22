-- V2: obras (artigos, livros, teses) lidas a partir de um link de PDF,
-- e o histórico de obras visitadas por usuário.

CREATE TABLE obras (
    id              BIGINT        GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fonte           VARCHAR(60)   NOT NULL,
    titulo          VARCHAR(300)  NOT NULL,
    autores         TEXT[]        NOT NULL DEFAULT '{}',
    ano             INTEGER       NOT NULL,
    tipo            VARCHAR(20)   NOT NULL,
    resumo          TEXT,
    doi             VARCHAR(100),
    url             VARCHAR(2000) NOT NULL,
    palavras_chave  TEXT[]        NOT NULL DEFAULT '{}',
    pdf_url         VARCHAR(2000),
    criado_por      BIGINT        REFERENCES usuarios (id) ON DELETE SET NULL,
    criado_em       TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT ck_obras_tipo CHECK (tipo IN ('Artigo', 'Livro', 'Tese')),
    CONSTRAINT ck_obras_ano CHECK (ano BETWEEN 1000 AND 2100),
    CONSTRAINT ck_obras_pdf_url CHECK (pdf_url IS NULL OR pdf_url ~* '^https?://')
);

CREATE INDEX ix_obras_ano ON obras (ano DESC);

-- Uma linha por (usuário, obra): visitar de novo só atualiza a data.
CREATE TABLE historico_visitas (
    usuario_id   BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    obra_id      BIGINT      NOT NULL REFERENCES obras (id) ON DELETE CASCADE,
    visitado_em  TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (usuario_id, obra_id)
);

CREATE INDEX ix_historico_usuario_data ON historico_visitas (usuario_id, visitado_em DESC);