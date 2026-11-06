package com.sistema.chamada.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrequenciaAluno {
    private Long alunoId;
    private int totalChamadas;
    private int totalPresencas;
    private int faltas;
    private double percentual;
    private boolean abaixoDoMinimo;
}