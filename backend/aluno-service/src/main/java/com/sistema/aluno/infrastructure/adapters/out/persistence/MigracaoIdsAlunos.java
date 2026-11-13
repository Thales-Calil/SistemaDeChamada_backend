package com.sistema.aluno.infrastructure.adapters.out.persistence;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonType;
import org.bson.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Migração única (2.1): alunos antigos foram gravados com _id ObjectId (texto).
 * Na subida, cada um recebe um id Long da sequência e o documento antigo é removido.
 * Depois que não houver mais nenhum ObjectId, não faz nada.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MigracaoIdsAlunos implements ApplicationRunner {

    private final MongoTemplate mongoTemplate;
    private final SequenciaAlunoGenerator sequencia;

    @Override
    public void run(ApplicationArguments args) {
        MongoCollection<Document> alunos = mongoTemplate.getCollection("alunos");

        Document maior = alunos.find(Filters.type("_id", BsonType.INT64)).sort(Sorts.descending("_id")).limit(1).first();
        if (maior != null) {
            sequencia.garantirMinimo(((Number) maior.get("_id")).longValue());
        }

        List<Document> antigos = alunos.find(Filters.type("_id", BsonType.OBJECT_ID)).into(new ArrayList<>());
        if (antigos.isEmpty()) {
            return;
        }
        log.warn("Migrando {} aluno(s) com id ObjectId para id Long...", antigos.size());
        for (Document antigo : antigos) {
            Object idAntigo = antigo.get("_id");
            long novoId = sequencia.proximo();
            Document novo = new Document(antigo);
            novo.put("_id", novoId);
            alunos.insertOne(novo);
            alunos.deleteOne(Filters.eq("_id", idAntigo));
            log.info("Aluno {} migrado: {} -> {}", antigo.getString("nome"), idAntigo, novoId);
        }
    }
}
