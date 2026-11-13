package com.sistema.chamada.infrastructure.adapters.in.web.dto;

import com.sistema.chamada.application.ports.in.FrequenciaTurma;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Frequência do aluno em uma turma (tela do aluno). */
@Data
@NoArgsConstructor
public class FrequenciaAlunoTurmaDTO {
    private Long turmaId;
    private String turmaNome;
    private String disciplinaNome;
    private Long alunoId;
    private int totalChamadas;
    private int totalPresencas;
    private int faltas;
    private double percentual;
    private boolean alertaRiscoReprovacao;

    public FrequenciaAlunoTurmaDTO(FrequenciaTurma f) {
        this.turmaId = f.turmaId();
        this.turmaNome = f.turmaNome();
        this.disciplinaNome = f.disciplinaNome();
        this.alunoId = f.frequencia().getAlunoId();
        this.totalChamadas = f.frequencia().getTotalChamadas();
        this.totalPresencas = f.frequencia().getTotalPresencas();
        this.faltas = f.frequencia().getFaltas();
        this.percentual = f.frequencia().getPercentual();
        this.alertaRiscoReprovacao = f.frequencia().isAbaixoDoMinimo();
    }
}
