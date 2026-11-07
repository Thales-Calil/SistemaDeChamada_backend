package com.sistema.aluno.infrastructure.adapters.in.web.controller;

import com.sistema.aluno.application.ports.in.GerenciarAlunoUseCase;
import com.sistema.aluno.domain.model.Aluno;
import com.sistema.aluno.infrastructure.adapters.in.web.AlunoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AlunoControllerTest {

    private MockMvc mockMvc;
    private GerenciarAlunoUseCase gerenciarAlunoUseCase;

    @BeforeEach
    void setUp() {
        // 1. Cria o mock com Mockito puro (sem Spring)
        gerenciarAlunoUseCase = Mockito.mock(GerenciarAlunoUseCase.class);

        // 2. Instancia o Controller manualmente
        AlunoController controller = new AlunoController(gerenciarAlunoUseCase);

        // 3. Monta o MockMvc standalone
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deveCriarAlunoERetornar201() throws Exception {
        Aluno aluno = new Aluno();
        aluno.setId("65a1f2b3c4d5e6f7a8b9c0d1");

        when(gerenciarAlunoUseCase.cadastrar(any(Aluno.class))).thenReturn(aluno);

        String json = """
                {
                    "nome": "Thales Calil",
                    "matricula": "2026001",
                    "email": "thales@pucpr.edu.br"
                }
                """;

        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("65a1f2b3c4d5e6f7a8b9c0d1"));
    }

    @Test
    void deveBuscarPorIdERetornar200() throws Exception {
        Aluno aluno = new Aluno();
        aluno.setNome("Thales Calil");

        when(gerenciarAlunoUseCase.buscarPorId("65a1f2b3c4d5e6f7a8b9c0d1")).thenReturn(aluno);

        mockMvc.perform(get("/api/alunos/{id}", "65a1f2b3c4d5e6f7a8b9c0d1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Thales Calil"));
    }
}