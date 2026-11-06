package com.sistema.turma.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "turmas")
public class TurmaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private ProfessorEntity professor;

    @ManyToOne
    @JoinColumn(name = "disciplina_id", nullable = false)
    private DisciplinaEntity disciplina;
}