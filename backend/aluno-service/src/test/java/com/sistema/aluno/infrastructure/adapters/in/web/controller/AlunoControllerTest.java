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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AlunoControllerTest {

    private MockMvc mockMvc;
    private GerenciarAlunoUseCase gerenciarAlunoUseCase;

    @BeforeEach
    void setUp() {
        gerenciarAlunoUseCase = Mockito.mock(GerenciarAlunoUseCase.class);
        AlunoController controller = new AlunoController(gerenciarAlunoUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deveCriarAlunoERetornar201ComIdNumerico() throws Exception {
        Aluno aluno = new Aluno();
        aluno.setId(42L);

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
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void deveBuscarPorIdERetornar200() throws Exception {
        Aluno aluno = new Aluno();
        aluno.setId(42L);
        aluno.setNome("Thales Calil");

        when(gerenciarAlunoUseCase.buscarPorId(42L)).thenReturn(aluno);

        mockMvc.perform(get("/api/alunos/{id}", 42L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Thales Calil"));
    }

    @Test
    void deveFiltrarPorIds() throws Exception {
        Aluno aluno = new Aluno();
        aluno.setId(42L);
        when(gerenciarAlunoUseCase.listar(List.of(42L, 43L))).thenReturn(List.of(aluno));

        mockMvc.perform(get("/api/alunos").param("ids", "42,43"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(42));
    }
}
