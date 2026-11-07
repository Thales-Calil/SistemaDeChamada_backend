package com.sistema.chamada.infrastructure.adapters.in.web.controller;

import com.sistema.chamada.application.ports.in.GerenciarChamadaUseCase;
import com.sistema.chamada.application.ports.in.GerenciarPresencaUseCase;
import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.domain.model.StatusChamada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ChamadaControllerTest {

    private MockMvc mockMvc;
    private GerenciarChamadaUseCase gerenciarChamadaUseCase;
    private GerenciarPresencaUseCase gerenciarPresencaUseCase;

    @BeforeEach
    void setUp() {
        // Mock das duas dependências exigidas pelo @RequiredArgsConstructor do Controller
        gerenciarChamadaUseCase = Mockito.mock(GerenciarChamadaUseCase.class);
        gerenciarPresencaUseCase = Mockito.mock(GerenciarPresencaUseCase.class);

        ChamadaController controller = new ChamadaController(gerenciarChamadaUseCase, gerenciarPresencaUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private Chamada criarChamadaMock() {
        Chamada chamada = new Chamada();
        chamada.setId(1L);
        chamada.setTurmaId(10L);
        chamada.setData(LocalDate.now());
        chamada.setHorario(LocalTime.of(19, 0));
        chamada.setStatus(StatusChamada.ABERTA);
        return chamada;
    }

    @Test
    void deveCriarChamadaERetornar201() throws Exception {
        Chamada chamada = criarChamadaMock();

        when(gerenciarChamadaUseCase.criar(any(Chamada.class))).thenReturn(chamada);

        String json = """
                {
                    "turmaId": 10,
                    "data": "2026-11-07",
                    "horario": "19:00:00"
                }
                """;

        mockMvc.perform(post("/api/chamadas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveBuscarPorIdERetornar200() throws Exception {
        Chamada chamada = criarChamadaMock();

        when(gerenciarChamadaUseCase.buscarPorId(1L)).thenReturn(chamada);

        mockMvc.perform(get("/api/chamadas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turmaId").value(10));
    }
}