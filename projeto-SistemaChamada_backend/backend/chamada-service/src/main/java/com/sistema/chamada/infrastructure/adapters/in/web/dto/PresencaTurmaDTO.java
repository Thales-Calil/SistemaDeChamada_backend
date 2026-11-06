package com.sistema.chamada.infrastructure.adapters.in.web.dto;

import com.sistema.chamada.domain.model.FrequenciaAluno;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PresencaTurmaDTO {
    private Long alunoId;
    private int totalChamadas;
    private int totalPresencas;
    private int faltas;
    private double percentual;
    private boolean alertaRiscoReprovacao; // Frontend pode usar isto para pintar a linha de vermelho

    public PresencaTurmaDTO(FrequenciaAluno frequencia) {
        this.alunoId = frequencia.getAlunoId();
        this.totalChamadas = frequencia.getTotalChamadas();
        this.totalPresencas = frequencia.getTotalPresencas();
        this.faltas = frequencia.getFaltas();
        this.percentual = frequencia.getPercentual();
        this.alertaRiscoReprovacao = frequencia.isAbaixoDoMinimo();
    }
}