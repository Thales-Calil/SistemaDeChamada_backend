package com.sistema.turma.infrastructure.adapters.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaId implements Serializable {
    private Long turmaId;
    private Long alunoId;
}
