package com.sistema.chamada.application.ports.out;

import com.sistema.chamada.application.security.UsuarioLogado;

/** Porta para obter o usuário da requisição atual (implementada com Spring Security). */
public interface UsuarioLogadoPort {
    UsuarioLogado atual();
}
