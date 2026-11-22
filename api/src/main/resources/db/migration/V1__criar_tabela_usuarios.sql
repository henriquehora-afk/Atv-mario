-- V1: tabela de usuários (id sequencial gerado pelo banco).
CREATE TABLE usuarios (
    id             BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome           VARCHAR(150) NOT NULL,
    email          VARCHAR(254) NOT NULL,
    senha_hash     VARCHAR(100) NOT NULL,
    criado_em      TIMESTAMPTZ  NOT NULL,
    atualizado_em  TIMESTAMPTZ  NOT NULL,

    CONSTRAINT uk_usuarios_email UNIQUE (email),
    CONSTRAINT ck_usuarios_email_minusculo CHECK (email = lower(email)),
    CONSTRAINT ck_usuarios_atualizado_apos_criado CHECK (atualizado_em >= criado_em)
);