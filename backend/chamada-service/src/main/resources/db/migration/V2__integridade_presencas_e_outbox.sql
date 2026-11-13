-- 2.6: remove presenças duplicadas (mesmo aluno 2x na mesma chamada), mantendo o registro mais recente
WITH duplicadas AS (
    SELECT id, ROW_NUMBER() OVER (PARTITION BY chamada_id, aluno_id ORDER BY id DESC) AS ordem
    FROM presencas
)
DELETE FROM duplicadas WHERE ordem > 1;

-- 2.7: remove presenças órfãs (de chamadas que não existem mais)
DELETE p FROM presencas p WHERE NOT EXISTS (SELECT 1 FROM chamadas c WHERE c.id = p.chamada_id);

-- 2.6: um aluno só pode ter UMA presença por chamada
ALTER TABLE presencas ADD CONSTRAINT uq_presencas_chamada_aluno UNIQUE (chamada_id, aluno_id);
ALTER TABLE presencas ADD CONSTRAINT fk_presencas_chamada FOREIGN KEY (chamada_id) REFERENCES chamadas (id) ON DELETE CASCADE;

CREATE INDEX ix_chamadas_turma  ON chamadas  (turma_id);
CREATE INDEX ix_presencas_aluno ON presencas (aluno_id);

-- 2.4: Outbox - eventos gravados junto com a chamada e enviados depois ao Service Bus
CREATE TABLE outbox_eventos (
    id          BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    tipo        VARCHAR(100)   NOT NULL,
    chave       VARCHAR(200)   NOT NULL,
    payload     NVARCHAR(MAX)  NOT NULL,
    criado_em   DATETIME2      NOT NULL,
    enviado_em  DATETIME2      NULL,
    tentativas  INT            NOT NULL CONSTRAINT df_outbox_tentativas DEFAULT 0,
    ultimo_erro NVARCHAR(1000) NULL,
    CONSTRAINT uq_outbox_chave UNIQUE (chave)
);
CREATE INDEX ix_outbox_pendentes ON outbox_eventos (enviado_em, id);
