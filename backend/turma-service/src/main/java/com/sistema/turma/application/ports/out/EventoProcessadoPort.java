package com.sistema.turma.application.ports.out;

/** Idempotência do consumidor do Service Bus: evita processar o mesmo evento duas vezes. */
public interface EventoProcessadoPort {
    /** Registra a chave e retorna true; se a chave já existia, retorna false. */
    boolean registrarSeNovo(String chave);
}
