package com.sistema.chamada.application.usecase.service;

import com.sistema.chamada.application.exception.AcessoNegadoException;
import com.sistema.chamada.application.ports.out.TurmaInfo;
import com.sistema.chamada.application.ports.out.TurmaServicePort;
import com.sistema.chamada.application.ports.out.UsuarioLogadoPort;
import com.sistema.chamada.application.security.UsuarioLogado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Regra única de acesso às chamadas de uma turma:
 * ADMIN -> qualquer turma | PROFESSOR -> só as turmas dele | ALUNO -> não gerencia chamadas.
 * Também garante (2.7) que a turma existe no turma-service.
 */
@Component
@RequiredArgsConstructor
public class AcessoTurma {

    private final TurmaServicePort turmaServicePort;
    private final UsuarioLogadoPort usuarioLogadoPort;

    public TurmaInfo verificar(Long turmaId) {
        UsuarioLogado u = usuarioLogadoPort.atual();
        if (u.isAluno()) {
            throw new AcessoNegadoException("Aluno não tem acesso ao gerenciamento de chamadas.");
        }
        TurmaInfo turma = turmaServicePort.buscarTurma(turmaId); // 404 se a turma não existir
        if (u.isProfessor() && !turma.professorId().equals(u.referenciaId())) {
            throw new AcessoNegadoException("A turma " + turmaId + " não é sua.");
        }
        return turma;
    }
}
