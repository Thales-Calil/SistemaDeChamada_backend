package com.sistema.aluno.domain.model;

import lombok.Data;

@Data
public class Aluno {

    /** Id numérico (Long), igual em todos os microsserviços. Gerado por sequência no Cosmos DB. */
    private Long id;

    private String nome;

    private String matricula;

    private String email;
}
