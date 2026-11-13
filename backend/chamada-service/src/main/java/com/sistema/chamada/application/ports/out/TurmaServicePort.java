package com.sistema.chamada.application.ports.out;

import java.util.List;

/** Consulta ao turma-service (dono das turmas e matrículas). */
public interface TurmaServicePort {
    /** Lança NaoEncontradoException (404) ou AcessoNegadoException (403) conforme o turma-service. */
    TurmaInfo buscarTurma(Long turmaId);
    List<Long> alunosDaTurma(Long turmaId);
    List<TurmaInfo> turmasDoAluno(Long alunoId);
}
