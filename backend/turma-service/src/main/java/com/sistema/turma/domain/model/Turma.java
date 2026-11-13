package com.sistema.turma.domain.model;

import lombok.Data;

@Data
public class Turma {

    private Long id;

    private String nome;

    private Professor professor;

    private Disciplina disciplina;

    /** Quantidade de chamadas finalizadas (atualizada pelo evento ChamadaFinalizada do Service Bus). */
    private int aulasMinistradas;
}
