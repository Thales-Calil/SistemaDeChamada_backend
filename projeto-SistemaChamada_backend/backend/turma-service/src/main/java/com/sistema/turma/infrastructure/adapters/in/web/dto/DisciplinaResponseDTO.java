package com.sistema.turma.infrastructure.adapters.in.web.dto;

import com.sistema.turma.domain.model.Disciplina;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DisciplinaResponseDTO {

    private Long id;
    private String nome;
    private String codigo;

    public DisciplinaResponseDTO(Disciplina disciplina) {
        this.id = disciplina.getId();
        this.nome = disciplina.getNome();
        this.codigo = disciplina.getCodigo();
    }
}