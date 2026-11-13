-- V1: esquema que o Hibernate (ddl-auto=update) já criava.
-- Em bancos que já existiam, o Flyway NÃO executa este arquivo (baseline-version=1).
CREATE TABLE professores (
    id    BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    nome  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT uk_professores_email UNIQUE (email)
);

CREATE TABLE disciplinas (
    id     BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    nome   VARCHAR(255) NOT NULL,
    codigo VARCHAR(255) NOT NULL,
    CONSTRAINT uk_disciplinas_codigo UNIQUE (codigo)
);

CREATE TABLE turmas (
    id            BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    nome          VARCHAR(255) NOT NULL,
    professor_id  BIGINT NOT NULL,
    disciplina_id BIGINT NOT NULL,
    CONSTRAINT fk_turmas_professor  FOREIGN KEY (professor_id)  REFERENCES professores (id),
    CONSTRAINT fk_turmas_disciplina FOREIGN KEY (disciplina_id) REFERENCES disciplinas (id)
);
