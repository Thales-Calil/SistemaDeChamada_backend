package com.sistema.turma.application.ports.out;

/** Consulta ao aluno-service (Cosmos DB). */
public interface AlunoServicePort {
    boolean existeAluno(Long alunoId);
}
