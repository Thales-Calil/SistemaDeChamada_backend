package com.sistema.chamada.application.ports.out;

/** Dados da turma vindos do turma-service. */
public record TurmaInfo(Long id, String nome, Long professorId, String disciplinaNome) {
}
