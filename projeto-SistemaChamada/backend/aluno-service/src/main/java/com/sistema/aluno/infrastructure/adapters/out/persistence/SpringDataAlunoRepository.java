package com.sistema.aluno.infrastructure.adapters.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataAlunoRepository extends MongoRepository<AlunoEntity, String> {
}