package com.sistema.turma.infrastructure.adapters.out.persistence.adapter;

import com.sistema.turma.application.ports.out.MatriculaRepositoryPort;
import com.sistema.turma.infrastructure.adapters.out.persistence.entity.MatriculaEntity;
import com.sistema.turma.infrastructure.adapters.out.persistence.repository.SpringDataMatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MatriculaPersistenceAdapter implements MatriculaRepositoryPort {

    private final SpringDataMatriculaRepository repository;

    @Override
    public List<Long> listarAlunoIds(Long turmaId) {
        return repository.findByTurmaId(turmaId).stream().map(MatriculaEntity::getAlunoId).sorted().toList();
    }

    @Override
    public List<Long> listarAlunoIdsDasTurmas(Collection<Long> turmaIds) {
        return repository.findByTurmaIdIn(turmaIds).stream().map(MatriculaEntity::getAlunoId).toList();
    }

    @Override
    public List<Long> listarTurmaIdsDoAluno(Long alunoId) {
        return repository.findByAlunoId(alunoId).stream().map(MatriculaEntity::getTurmaId).toList();
    }

    @Override
    public boolean existe(Long turmaId, Long alunoId) {
        return repository.existsByTurmaIdAndAlunoId(turmaId, alunoId);
    }

    @Override
    @Transactional
    public void salvar(Long turmaId, Long alunoId) {
        repository.save(new MatriculaEntity(turmaId, alunoId, LocalDateTime.now()));
    }

    @Override
    @Transactional
    public void excluir(Long turmaId, Long alunoId) {
        repository.excluir(turmaId, alunoId);
    }

    @Override
    @Transactional
    public void excluirPorTurma(Long turmaId) {
        repository.excluirPorTurma(turmaId);
    }
}
