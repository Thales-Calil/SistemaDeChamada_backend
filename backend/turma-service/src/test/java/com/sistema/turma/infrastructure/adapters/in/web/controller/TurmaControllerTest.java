package com.sistema.turma.infrastructure.adapters.in.web.controller;

import com.sistema.turma.application.ports.in.GerenciarMatriculaUseCase;
import com.sistema.turma.application.ports.in.GerenciarTurmaUseCase;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.domain.model.Turma;
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

class TurmaControllerTest {

    private MockMvc mockMvc;
    private GerenciarTurmaUseCase gerenciarTurmaUseCase;
    private GerenciarMatriculaUseCase gerenciarMatriculaUseCase;

    @BeforeEach
    void setUp() {
        gerenciarTurmaUseCase = Mockito.mock(GerenciarTurmaUseCase.class);
        gerenciarMatriculaUseCase = Mockito.mock(GerenciarMatriculaUseCase.class);
        TurmaController controller = new TurmaController(gerenciarTurmaUseCase, gerenciarMatriculaUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private Turma criarTurmaMock() {
        Professor professor = new Professor();
        professor.setId(5L);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(10L);

        Turma turma = new Turma();
        turma.setId(1L);
        turma.setNome("Arquitetura de Soluções");
        turma.setProfessor(professor);
        turma.setDisciplina(disciplina);
        return turma;
    }

    @Test
    void deveCadastrarTurmaERetornar201() throws Exception {
        Turma turma = criarTurmaMock();

        when(gerenciarTurmaUseCase.cadastrar(any(Turma.class))).thenReturn(turma);

        String json = """
                {
                    "nome": "Arquitetura de Soluções",
                    "disciplinaId": 10,
                    "professorId": 5
                }
                """;

        mockMvc.perform(post("/api/turmas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveBuscarPorIdERetornar200() throws Exception {
        Turma turma = criarTurmaMock();

        when(gerenciarTurmaUseCase.buscarPorId(1L)).thenReturn(turma);

        mockMvc.perform(get("/api/turmas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Arquitetura de Soluções"));
    }

    @Test
    void deveListarIdsDosAlunosDaTurma() throws Exception {
        when(gerenciarMatriculaUseCase.listarAlunosDaTurma(1L)).thenReturn(java.util.List.of(42L, 43L));

        mockMvc.perform(get("/api/turmas/{id}/alunos", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(42))
                .andExpect(jsonPath("$[1]").value(43));
    }
}
