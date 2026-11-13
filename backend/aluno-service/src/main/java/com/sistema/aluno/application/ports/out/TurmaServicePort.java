package com.sistema.aluno.application.ports.out;

import java.util.List;

/** Consulta ao turma-service (dono das matrículas aluno <-> turma). */
public interface TurmaServicePort {
    List<Long> alunosDoProfessor(Long professorId);
    boolean alunoPossuiTurmas(Long alunoId);
}
