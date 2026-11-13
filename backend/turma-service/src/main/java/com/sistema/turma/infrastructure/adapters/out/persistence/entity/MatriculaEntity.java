package com.sistema.turma.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Tabela turma_alunos: aluno (id do aluno-service) matriculado em uma turma. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "turma_alunos")
@IdClass(MatriculaId.class)
public class MatriculaEntity {

    @Id
    @Column(name = "turma_id")
    private Long turmaId;

    @Id
    @Column(name = "aluno_id")
    private Long alunoId;

    @Column(name = "matriculado_em", nullable = false)
    private LocalDateTime matriculadoEm;
}
