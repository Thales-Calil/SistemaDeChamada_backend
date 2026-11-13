package com.sistema.aluno.infrastructure.adapters.out.persistence;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "alunos")
public class AlunoEntity {

    @Id
    private Long id; // Long gerado pela sequência "contadores" (antes era ObjectId em String)

    private String nome;
    private String matricula;
    private String email;
}
