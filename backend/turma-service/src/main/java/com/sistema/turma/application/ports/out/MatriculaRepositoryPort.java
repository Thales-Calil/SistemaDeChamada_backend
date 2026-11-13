package com.sistema.turma.application.ports.out;

import java.util.Collection;
import java.util.List;

public interface MatriculaRepositoryPort {
    List<Long> listarAlunoIds(Long turmaId);
    List<Long> listarAlunoIdsDasTurmas(Collection<Long> turmaIds);
    List<Long> listarTurmaIdsDoAluno(Long alunoId);
    boolean existe(Long turmaId, Long alunoId);
    void salvar(Long turmaId, Long alunoId);
    void excluir(Long turmaId, Long alunoId);
    void excluirPorTurma(Long turmaId);
}
