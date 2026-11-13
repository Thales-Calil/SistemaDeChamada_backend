package com.sistema.aluno.infrastructure.adapters.out.persistence;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Gera ids Long sequenciais no Cosmos DB (API MongoDB), que não tem auto-incremento.
 * Usa um documento contador { _id: "alunos", seq: N } atualizado de forma atômica ($inc).
 */
@Component
@RequiredArgsConstructor
public class SequenciaAlunoGenerator {

    static final String COLECAO = "contadores";
    static final String CHAVE = "alunos";

    private final MongoTemplate mongoTemplate;

    public long proximo() {
        Document doc = contadores().findOneAndUpdate(
                Filters.eq("_id", CHAVE),
                Updates.inc("seq", 1L),
                new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
        return ((Number) doc.get("seq")).longValue();
    }

    /** Garante que a sequência nunca gere um id menor ou igual a um já existente. */
    public void garantirMinimo(long valor) {
        contadores().updateOne(Filters.eq("_id", CHAVE), Updates.max("seq", valor), new UpdateOptions().upsert(true));
    }

    private MongoCollection<Document> contadores() {
        return mongoTemplate.getCollection(COLECAO);
    }
}
