package com.sistema.chamada.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "presencas")
public class PresencaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long chamadaId;

    @Column(nullable = false)
    private Long alunoId;

    @Column(nullable = false)
    private Boolean presente;
}