-- Fusão do chamada-service no turma-service: chamadas e presenças passam a morar no mesmo banco das turmas.
-- Com tudo no mesmo banco, a integridade com turmas vira chave estrangeira de verdade.

CREATE TABLE chamadas (
    id       BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    turma_id BIGINT       NOT NULL,
    data     DATE         NOT NULL,
    horario  TIME         NOT NULL,
    status   VARCHAR(20)  NOT NULL,
    CONSTRAINT ck_chamadas_status CHECK (status IN ('ABERTA', 'FINALIZADA')),
    -- Turma com chamadas não pode ser excluída (sem ON DELETE CASCADE de propósito)
    CONSTRAINT fk_chamadas_turma FOREIGN KEY (turma_id) REFERENCES turmas (id),
    -- Não existem duas chamadas da mesma turma no mesmo dia e horário
    CONSTRAINT uq_chamadas_turma_data_horario UNIQUE (turma_id, data, horario)
);
CREATE INDEX ix_chamadas_turma ON chamadas (turma_id);

-- aluno_id é o id (Long) do aluno no aluno-service (Cosmos DB): outro banco, então sem FK para alunos.
-- A matrícula do aluno na turma da chamada é validada na aplicação (turma_alunos) antes de gravar.
CREATE TABLE presencas (
    id         BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    chamada_id BIGINT NOT NULL,
    aluno_id   BIGINT NOT NULL,
    presente   BIT    NOT NULL,
    CONSTRAINT fk_presencas_chamada FOREIGN KEY (chamada_id) REFERENCES chamadas (id) ON DELETE CASCADE,
    -- Um aluno tem no máximo uma presença por chamada
    CONSTRAINT uq_presencas_chamada_aluno UNIQUE (chamada_id, aluno_id)
);
CREATE INDEX ix_presencas_aluno ON presencas (aluno_id);
