package com.sistema.turma.infrastructure.adapters.out.http;

import com.sistema.turma.application.ports.out.ChamadaServicePort;
import com.sistema.turma.infrastructure.http.ServicoHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ChamadaServiceClient implements ChamadaServicePort {

    private final ServicoHttpClient http;

    public ChamadaServiceClient(@Value("${services.chamada.url}") String url) {
        this.http = new ServicoHttpClient("chamada-service", url);
    }

    @Override
    public boolean turmaPossuiChamadas(Long turmaId) {
        List<Map<String, Object>> chamadas = http.get("/api/chamadas/turma/{turmaId}",
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}, turmaId);
        return chamadas != null && !chamadas.isEmpty();
    }
}
