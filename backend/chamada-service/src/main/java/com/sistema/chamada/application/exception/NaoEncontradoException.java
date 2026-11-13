package com.sistema.chamada.application.exception;

/** Recurso inexistente -> HTTP 404. */
public class NaoEncontradoException extends RuntimeException {
    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
