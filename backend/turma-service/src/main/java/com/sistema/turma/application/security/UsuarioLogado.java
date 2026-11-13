package com.sistema.turma.application.security;

/**
 * Usuário extraído do JWT emitido pelo API Gateway.
 * referenciaId = id do aluno (perfil ALUNO) ou do professor (perfil PROFESSOR); nulo para ADMIN.
 */
public record UsuarioLogado(Long usuarioId, String username, Perfil perfil, Long referenciaId) {

    public boolean isAdmin() {
        return perfil == Perfil.ADMIN;
    }

    public boolean isProfessor() {
        return perfil == Perfil.PROFESSOR;
    }

    public boolean isAluno() {
        return perfil == Perfil.ALUNO;
    }
}
