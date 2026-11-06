package com.sistema.turma.domain.model;

import lombok.Data;

@Data
public class Turma {

    private Long id;

    private String nome;

    private Professor professor;

    private Disciplina disciplina;
}