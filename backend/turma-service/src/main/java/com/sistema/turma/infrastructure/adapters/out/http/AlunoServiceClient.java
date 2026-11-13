package com.sistema.turma.infrastructure.adapters.out.http;

import com.sistema.turma.application.exception.NaoEncontradoException;
import com.sistema.turma.application.ports.out.AlunoServicePort;
import com.sistema.turma.infrastructure.http.ServicoHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AlunoServiceClient implements AlunoServicePort {

    private final ServicoHttpClient http;

    public AlunoServiceClient(@Value("${services.aluno.url}") String url) {
        this.http = new ServicoHttpClient("aluno-service", url);
    }

    @Override
    public boolean existeAluno(Long alunoId) {
        try {
            http.get("/api/alunos/{id}", String.class, alunoId);
            return true;
        } catch (NaoEncontradoException e) {
            return false;
        }
    }
}
