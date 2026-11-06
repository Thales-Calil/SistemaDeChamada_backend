package com.sistema.aluno.infrastructure.adapters.out.persistence;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Data
@Document(collection="alunos")
public class AlunoEntity {

    @Id
    private String id; // Mongo usa String (ObjectId) em vez de Long

    private String nome;
    private String matricula;
    private String email;
}
