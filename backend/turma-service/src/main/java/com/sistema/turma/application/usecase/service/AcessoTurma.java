package com.sistema.turma.application.usecase.service;

import com.sistema.turma.application.exception.AcessoNegadoException;
import com.sistema.turma.application.exception.NaoEncontradoException;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.application.ports.out.UsuarioLogadoPort;
import com.sistema.turma.application.security.UsuarioLogado;
import com.sistema.turma.domain.model.Turma;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Regra única de acesso às chamadas e presenças de uma turma (consulta local, sem HTTP):
 * ADMIN -> qualquer turma | PROFESSOR -> só as turmas dele | ALUNO -> não gerencia chamadas.
 */
@Component
@RequiredArgsConstructor
public class AcessoTurma {

    private final TurmaRepositoryPort turmaRepositoryPort;
    private final UsuarioLogadoPort usuarioLogadoPort;

    public Turma verificar(Long turmaId) {
        UsuarioLogado u = usuarioLogadoPort.atual();
        if (u.isAluno()) {
            throw new AcessoNegadoException("Aluno não tem acesso ao gerenciamento de chamadas.");
        }
        Turma turma = turmaRepositoryPort.buscarPorId(turmaId)
                .orElseThrow(() -> new NaoEncontradoException("Turma " + turmaId + " não encontrada."));
        if (u.isProfessor() && !turma.getProfessor().getId().equals(u.referenciaId())) {
            throw new AcessoNegadoException("A turma " + turmaId + " não é sua.");
        }
        return turma;
    }
}
