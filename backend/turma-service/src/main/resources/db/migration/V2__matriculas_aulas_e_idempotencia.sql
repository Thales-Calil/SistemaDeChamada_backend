-- 2.2: vínculo aluno <-> turma. aluno_id é o id (Long) do aluno no aluno-service (Cosmos DB),
-- por isso não há FK para alunos (outro banco). A FK para turmas apaga as matrículas junto com a turma.
CREATE TABLE turma_alunos (
    turma_id       BIGINT    NOT NULL,
    aluno_id       BIGINT    NOT NULL,
    matriculado_em DATETIME2 NOT NULL CONSTRAINT df_turma_alunos_data DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_turma_alunos PRIMARY KEY (turma_id, aluno_id),
    CONSTRAINT fk_turma_alunos_turma FOREIGN KEY (turma_id) REFERENCES turmas (id) ON DELETE CASCADE
);
CREATE INDEX ix_turma_alunos_aluno ON turma_alunos (aluno_id);

-- Contador atualizado pelo evento ChamadaFinalizada (Service Bus)
ALTER TABLE turmas ADD aulas_ministradas INT NOT NULL CONSTRAINT df_turmas_aulas DEFAULT 0;

CREATE INDEX ix_turmas_professor ON turmas (professor_id);

-- 2.4: idempotência do consumidor (cada evento é processado uma única vez)
CREATE TABLE eventos_processados (
    chave         VARCHAR(200) NOT NULL PRIMARY KEY,
    processado_em DATETIME2    NOT NULL
);
