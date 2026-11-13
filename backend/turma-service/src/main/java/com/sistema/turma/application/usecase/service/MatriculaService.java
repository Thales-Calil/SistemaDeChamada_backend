package com.sistema.turma.application.usecase.service;

import com.sistema.turma.application.exception.AcessoNegadoException;
import com.sistema.turma.application.exception.NaoEncontradoException;
import com.sistema.turma.application.exception.RegraNegocioException;
import com.sistema.turma.application.ports.in.GerenciarMatriculaUseCase;
import com.sistema.turma.application.ports.out.AlunoServicePort;
import com.sistema.turma.application.ports.out.MatriculaRepositoryPort;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.application.ports.out.UsuarioLogadoPort;
import com.sistema.turma.application.security.UsuarioLogado;
import com.sistema.turma.domain.model.Turma;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatriculaService implements GerenciarMatriculaUseCase {

    private final TurmaRepositoryPort turmaRepositoryPort;
    private final MatriculaRepositoryPort matriculaRepositoryPort;
    private final AlunoServicePort alunoServicePort;
    private final UsuarioLogadoPort usuarioLogadoPort;

    @Override
    public List<Long> listarAlunosDaTurma(Long turmaId) {
        Turma turma = buscarTurma(turmaId);
        UsuarioLogado u = usuarioLogadoPort.atual();
        boolean professorDaTurma = u.isProfessor() && turma.getProfessor().getId().equals(u.referenciaId());
        if (!u.isAdmin() && !professorDaTurma) {
            throw new AcessoNegadoException("Somente o professor da turma ou ADMIN podem ver os alunos da turma " + turmaId + ".");
        }
        return matriculaRepositoryPort.listarAlunoIds(turmaId);
    }

    @Override
    @Transactional
    public void matricular(Long turmaId, Long alunoId) {
        exigirAdmin();
        buscarTurma(turmaId);
        if (matriculaRepositoryPort.existe(turmaId, alunoId)) {
            throw new RegraNegocioException("O aluno " + alunoId + " já está matriculado na turma " + turmaId + ".");
        }
        if (!alunoServicePort.existeAluno(alunoId)) {
            throw new NaoEncontradoException("Aluno " + alunoId + " não existe no aluno-service.");
        }
        matriculaRepositoryPort.salvar(turmaId, alunoId);
    }

    @Override
    @Transactional
    public void desmatricular(Long turmaId, Long alunoId) {
        exigirAdmin();
        if (!matriculaRepositoryPort.existe(turmaId, alunoId)) {
            throw new NaoEncontradoException("O aluno " + alunoId + " não está matriculado na turma " + turmaId + ".");
        }
        matriculaRepositoryPort.excluir(turmaId, alunoId);
    }

    @Override
    public List<Turma> listarTurmasDoAluno(Long alunoId) {
        UsuarioLogado u = usuarioLogadoPort.atual();
        if (u.isAluno() && !alunoId.equals(u.referenciaId())) {
            throw new AcessoNegadoException("Aluno só pode consultar as próprias turmas.");
        }
        List<Turma> turmas = turmaRepositoryPort.buscarPorIds(matriculaRepositoryPort.listarTurmaIdsDoAluno(alunoId));
        if (u.isProfessor()) {
            // professor só enxerga as turmas dele em que o aluno está
            return turmas.stream().filter(t -> t.getProfessor().getId().equals(u.referenciaId())).toList();
        }
        return turmas;
    }

    @Override
    public List<Long> listarAlunosDoProfessor(Long professorId) {
        UsuarioLogado u = usuarioLogadoPort.atual();
        boolean proprioProfessor = u.isProfessor() && professorId.equals(u.referenciaId());
        if (!u.isAdmin() && !proprioProfessor) {
            throw new AcessoNegadoException("Somente o próprio professor ou ADMIN podem consultar esta lista.");
        }
        List<Long> turmaIds = turmaRepositoryPort.buscarPorProfessorId(professorId).stream().map(Turma::getId).toList();
        if (turmaIds.isEmpty()) {
            return List.of();
        }
        return matriculaRepositoryPort.listarAlunoIdsDasTurmas(turmaIds).stream().distinct().toList();
    }

    private Turma buscarTurma(Long turmaId) {
        return turmaRepositoryPort.buscarPorId(turmaId)
                .orElseThrow(() -> new NaoEncontradoException("Turma " + turmaId + " não encontrada."));
    }

    private void exigirAdmin() {
        if (!usuarioLogadoPort.atual().isAdmin()) {
            throw new AcessoNegadoException("Somente ADMIN pode matricular ou remover alunos de turmas.");
        }
    }
}
