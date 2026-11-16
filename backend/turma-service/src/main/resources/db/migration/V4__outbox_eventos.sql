-- Padrão Outbox: o evento ChamadaFinalizada é gravado junto com a finalização da chamada
-- e enviado depois ao Azure Service Bus (tópico chamadas-topic, consumido pelo aluno-service).
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

-- O turma-service não consome mais o próprio evento (as aulas ministradas são somadas na mesma
-- transação da finalização), então a tabela de idempotência do consumidor deixa de ser usada.
DROP TABLE eventos_processados;
