package com.sistema.turma.application.usecase.service;

import com.sistema.turma.application.ports.in.GerenciarDisciplinaUseCase;
import com.sistema.turma.application.ports.out.DisciplinaRepositoryPort;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.domain.model.Disciplina;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplinaService implements GerenciarDisciplinaUseCase {

    private final DisciplinaRepositoryPort disciplinaRepositoryPort;
    private final TurmaRepositoryPort turmaRepositoryPort;

    @Override
    public List<Disciplina> listarTodos() {
        return disciplinaRepositoryPort.buscarTodos();
    }

    @Override
    public Disciplina buscarPorId(Long id) {
        return disciplinaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));
    }

    @Override
    public Disciplina cadastrar(Disciplina disciplina) {
        return disciplinaRepositoryPort.salvar(disciplina);
    }

    @Override
    public Disciplina atualizar(Long id, Disciplina disciplinaAtualizada) {
        Disciplina disciplinaExistente = buscarPorId(id);

        disciplinaExistente.setNome(disciplinaAtualizada.getNome());
        disciplinaExistente.setCodigo(disciplinaAtualizada.getCodigo());

        return disciplinaRepositoryPort.salvar(disciplinaExistente);
    }

    @Override
    public void excluir(Long id) {
        if (!disciplinaRepositoryPort.existePorId(id)) {
            throw new RuntimeException("Disciplina não encontrada");
        }

        // Regra de negócio: não excluir se estiver vinculada a uma turma
        if (turmaRepositoryPort.existePorDisciplinaId(id)) {
            throw new RuntimeException("Não é possível excluir a disciplina porque ela está vinculada a uma turma");
        }

        disciplinaRepositoryPort.excluir(id);
    }
}