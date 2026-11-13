package com.sistema.chamada.infrastructure.adapters.out.http;

import com.sistema.chamada.application.ports.out.TurmaInfo;
import com.sistema.chamada.application.ports.out.TurmaServicePort;
import com.sistema.chamada.infrastructure.http.ServicoHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TurmaServiceClient implements TurmaServicePort {

    private static final ParameterizedTypeReference<Map<String, Object>> MAPA = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA_MAPAS = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Long>> LISTA_IDS = new ParameterizedTypeReference<>() {};

    private final ServicoHttpClient http;

    public TurmaServiceClient(@Value("${services.turma.url}") String url) {
        this.http = new ServicoHttpClient("turma-service", url);
    }

    @Override
    public TurmaInfo buscarTurma(Long turmaId) {
        return paraTurmaInfo(http.get("/api/turmas/{id}", MAPA, turmaId));
    }

    @Override
    public List<Long> alunosDaTurma(Long turmaId) {
        List<Long> ids = http.get("/api/turmas/{id}/alunos", LISTA_IDS, turmaId);
        return ids == null ? List.of() : ids;
    }

    @Override
    public List<TurmaInfo> turmasDoAluno(Long alunoId) {
        List<Map<String, Object>> turmas = http.get("/api/turmas/aluno/{alunoId}", LISTA_MAPAS, alunoId);
        return turmas == null ? List.of() : turmas.stream().map(TurmaServiceClient::paraTurmaInfo).toList();
    }

    private static TurmaInfo paraTurmaInfo(Map<String, Object> json) {
        return new TurmaInfo(
                paraLong(json.get("id")),
                (String) json.get("nome"),
                paraLong(json.get("professorId")),
                (String) json.get("disciplinaNome"));
    }

    private static Long paraLong(Object valor) {
        if (valor == null) return null;
        if (valor instanceof Number n) return n.longValue();
        return Long.valueOf(valor.toString());
    }
}
