package com.sistema.chamada.domain.model;

import lombok.Data;

@Data
public class Presenca {

    private Long id;

    private Long chamadaId;

    private Long alunoId;

    private Boolean presente;
}