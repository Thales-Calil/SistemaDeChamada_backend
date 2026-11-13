package com.sistema.chamada.application.ports.in;

/** Um aluno da chamada em lote: presente ou ausente. */
public record ItemPresenca(Long alunoId, Boolean presente) {
}
