package com.sistema.turma.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "professores")
public class ProfessorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;
}
