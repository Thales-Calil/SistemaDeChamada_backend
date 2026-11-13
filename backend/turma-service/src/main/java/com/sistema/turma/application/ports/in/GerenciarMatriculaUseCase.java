package com.sistema.turma.application.ports.in;

import com.sistema.turma.domain.model.Turma;
import java.util.List;

/** Vínculo aluno <-> turma (o aluno mora no aluno-service; aqui guardamos apenas o id). */
public interface GerenciarMatriculaUseCase {
    List<Long> listarAlunosDaTurma(Long turmaId);
    void matricular(Long turmaId, Long alunoId);
    void desmatricular(Long turmaId, Long alunoId);
    List<Turma> listarTurmasDoAluno(Long alunoId);
    List<Long> listarAlunosDoProfessor(Long professorId);
}
