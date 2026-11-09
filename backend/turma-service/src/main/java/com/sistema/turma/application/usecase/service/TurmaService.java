package com.sistema.turma.application.usecase.service;

import com.sistema.turma.application.ports.in.GerenciarTurmaUseCase;
import com.sistema.turma.application.ports.out.DisciplinaRepositoryPort;
import com.sistema.turma.application.ports.out.ProfessorRepositoryPort;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.domain.model.Turma;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurmaService implements GerenciarTurmaUseCase {

    private final TurmaRepositoryPort turmaRepositoryPort;
    private final ProfessorRepositoryPort professorRepositoryPort;
    private final DisciplinaRepositoryPort disciplinaRepositoryPort;

    @Override
    public List<Turma> listarTodos() {
        return turmaRepositoryPort.buscarTodos();
    }

    @Override
    public Turma buscarPorId(Long id) {
        return turmaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
    }

    @Override
    public Turma cadastrar(Turma turmaInfo) {
        // Valida se o professor e a disciplina existem antes de montar a turma
        Professor professor = professorRepositoryPort.buscarPorId(turmaInfo.getProfessor().getId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Disciplina disciplina = disciplinaRepositoryPort.buscarPorId(turmaInfo.getDisciplina().getId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));

        turmaInfo.setProfessor(professor);
        turmaInfo.setDisciplina(disciplina);

        return turmaRepositoryPort.salvar(turmaInfo);
    }

    @Override
    public Turma atualizar(Long id, Turma turmaInfo) {
        Turma turmaExistente = buscarPorId(id);

        Professor professor = professorRepositoryPort.buscarPorId(turmaInfo.getProfessor().getId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Disciplina disciplina = disciplinaRepositoryPort.buscarPorId(turmaInfo.getDisciplina().getId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));

        turmaExistente.setNome(turmaInfo.getNome());
        turmaExistente.setProfessor(professor);
        turmaExistente.setDisciplina(disciplina);

        return turmaRepositoryPort.salvar(turmaExistente);
    }

    @Override
    public void excluir(Long id) {
        if (!turmaRepositoryPort.existePorId(id)) {
            throw new RuntimeException("Turma não encontrada");
        }
        turmaRepositoryPort.excluir(id);
    }
}