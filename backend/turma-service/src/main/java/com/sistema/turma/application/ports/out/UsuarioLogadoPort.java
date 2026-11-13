package com.sistema.turma.application.ports.out;

import com.sistema.turma.application.security.UsuarioLogado;

/** Porta para obter o usuário da requisição atual (implementada com Spring Security). */
public interface UsuarioLogadoPort {
    UsuarioLogado atual();
}
