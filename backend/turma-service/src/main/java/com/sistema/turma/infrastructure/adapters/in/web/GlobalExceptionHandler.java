package com.sistema.turma.infrastructure.adapters.in.web;

import com.sistema.turma.application.exception.AcessoNegadoException;
import com.sistema.turma.application.exception.NaoEncontradoException;
import com.sistema.turma.application.exception.RegraNegocioException;
import com.sistema.turma.application.exception.ServicoExternoException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Respostas de erro padronizadas (2.8). Em vez do 500 genérico, cada erro sai com o status
 * certo e um JSON dizendo o serviço, o caminho e o motivo. Erros inesperados são logados
 * com stack trace (aparecem no Log stream do App Service).
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String SERVICO = "turma-service";

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> naoEncontrado(NaoEncontradoException ex, HttpServletRequest req) {
        return corpo(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, Object>> regraNegocio(RegraNegocioException ex, HttpServletRequest req) {
        return corpo(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Map<String, Object>> acessoNegado(AcessoNegadoException ex, HttpServletRequest req) {
        return corpo(HttpStatus.FORBIDDEN, ex.getMessage(), req);
    }

    @ExceptionHandler(ServicoExternoException.class)
    public ResponseEntity<Map<String, Object>> servicoExterno(ServicoExternoException ex, HttpServletRequest req) {
        log.error("Falha de integração: {}", ex.getMessage(), ex);
        return corpo(HttpStatus.BAD_GATEWAY, ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> argumentoInvalido(IllegalArgumentException ex, HttpServletRequest req) {
        return corpo(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return corpo(HttpStatus.BAD_REQUEST, detalhes, req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> corpoInvalido(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return corpo(HttpStatus.BAD_REQUEST, "JSON inválido ou com tipo errado (ids devem ser números).", req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> tipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return corpo(HttpStatus.BAD_REQUEST, "Parâmetro '" + ex.getName() + "' inválido: " + ex.getValue(), req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridade(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violação de integridade em {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return corpo(HttpStatus.CONFLICT, "Operação conflita com dados existentes (registro duplicado ou vinculado).", req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> metodo(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return corpo(HttpStatus.METHOD_NOT_ALLOWED, "Método " + ex.getMethod() + " não suportado nesta rota.", req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> rotaInexistente(NoResourceFoundException ex, HttpServletRequest req) {
        return corpo(HttpStatus.NOT_FOUND, "Rota inexistente.", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> inesperado(Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado em {} {}", req.getMethod(), req.getRequestURI(), ex);
        return corpo(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno: " + ex.getClass().getSimpleName() + (ex.getMessage() != null ? " - " + ex.getMessage() : ""), req);
    }

    private ResponseEntity<Map<String, Object>> corpo(HttpStatus status, String mensagem, HttpServletRequest req) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now().toString());
        corpo.put("status", status.value());
        corpo.put("erro", status.getReasonPhrase());
        corpo.put("mensagem", mensagem == null ? "" : mensagem);
        corpo.put("servico", SERVICO);
        corpo.put("caminho", req.getRequestURI());
        return ResponseEntity.status(status).body(corpo);
    }
}
