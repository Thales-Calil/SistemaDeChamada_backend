package com.sistema.turma.application.exception;

/** Usuário autenticado, mas sem permissão para o recurso -> HTTP 403. */
public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
