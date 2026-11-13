-- V1: esquema que o Hibernate (ddl-auto=update) já criava.
-- Em bancos que já existiam, o Flyway NÃO executa este arquivo (baseline-version=1).
CREATE TABLE chamadas (
    id       BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    turma_id BIGINT       NOT NULL,
    data     DATE         NOT NULL,
    horario  TIME         NOT NULL,
    status   VARCHAR(255) NOT NULL,
    CONSTRAINT ck_chamadas_status CHECK (status IN ('ABERTA', 'FINALIZADA'))
);

CREATE TABLE presencas (
    id         BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    chamada_id BIGINT NOT NULL,
    aluno_id   BIGINT NOT NULL,
    presente   BIT    NOT NULL
);
