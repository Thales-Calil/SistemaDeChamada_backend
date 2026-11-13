package com.sistema.aluno.infrastructure.adapters.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataAlunoRepository extends MongoRepository<AlunoEntity, Long> {
    Optional<AlunoEntity> findFirstByMatricula(String matricula);
    Optional<AlunoEntity> findFirstByEmail(String email);
}
