package com.sistema.aluno.application.ports.out;

import com.sistema.aluno.application.security.UsuarioLogado;

/** Porta para obter o usuário da requisição atual (implementada com Spring Security). */
public interface UsuarioLogadoPort {
    UsuarioLogado atual();
}
