package com.sistema.chamada.domain.service;

import com.sistema.chamada.domain.model.FrequenciaAluno;

public class CalculadorFrequenciaDomainService {

    private static final double PERCENTUAL_MINIMO = 75.0; // RN08

    public FrequenciaAluno calcular(Long alunoId, int totalChamadas, int totalPresencas) {
        int faltas = calcularFaltas(totalChamadas, totalPresencas);
        double percentual = calcularPercentual(totalChamadas, totalPresencas);
        boolean abaixoDoMinimo = percentual < PERCENTUAL_MINIMO; // RN08

        return new FrequenciaAluno(
                alunoId,
                totalChamadas,
                totalPresencas,
                faltas,
                percentual,
                abaixoDoMinimo
        );
    }

    private int calcularFaltas(int totalChamadas, int totalPresencas) {
        // RN06: Faltas = TotalDeChamadas - TotalDePresenças
        return Math.max(0, totalChamadas - totalPresencas); // Math.max evita faltas negativas
    }

    private double calcularPercentual(int totalChamadas, int totalPresencas) {
        // RN09: Quando ainda não houver chamadas registradas, não deve realizar uma divisão por zero.
        if (totalChamadas == 0) {
            return 100.0; // Sem aulas cadastradas, assume-se 100% de frequência
        }

        // RN07: Frequência(%) = (TotalDePresenças / TotalDeChamadas) * 100
        return ((double) totalPresencas / totalChamadas) * 100.0;
    }
}