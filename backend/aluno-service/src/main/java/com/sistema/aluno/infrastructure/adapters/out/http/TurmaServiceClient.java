package com.sistema.aluno.infrastructure.adapters.out.http;

import com.sistema.aluno.application.ports.out.TurmaServicePort;
import com.sistema.aluno.infrastructure.http.ServicoHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TurmaServiceClient implements TurmaServicePort {

    private final ServicoHttpClient http;

    public TurmaServiceClient(@Value("${services.turma.url}") String url) {
        this.http = new ServicoHttpClient("turma-service", url);
    }

    @Override
    public List<Long> alunosDoProfessor(Long professorId) {
        List<Long> ids = http.get("/api/turmas/professor/{professorId}/alunos",
                new ParameterizedTypeReference<List<Long>>() {}, professorId);
        return ids == null ? List.of() : ids;
    }

    @Override
    public boolean alunoPossuiTurmas(Long alunoId) {
        List<Map<String, Object>> turmas = http.get("/api/turmas/aluno/{alunoId}",
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}, alunoId);
        return turmas != null && !turmas.isEmpty();
    }
}
