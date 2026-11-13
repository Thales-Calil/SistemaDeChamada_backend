package com.sistema.turma.application.exception;

/** Operação que viola uma regra de negócio (duplicidade, vínculo existente, chamada finalizada...) -> HTTP 409. */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
