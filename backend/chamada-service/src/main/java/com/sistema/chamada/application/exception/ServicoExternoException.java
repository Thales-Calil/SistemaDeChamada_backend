package com.sistema.chamada.application.exception;

/** Falha ao chamar outro microsserviço (fora do ar, timeout, erro 5xx) -> HTTP 502. */
public class ServicoExternoException extends RuntimeException {
    public ServicoExternoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
