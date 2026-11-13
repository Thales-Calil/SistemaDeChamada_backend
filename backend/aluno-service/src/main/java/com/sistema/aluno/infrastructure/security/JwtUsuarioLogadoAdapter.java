package com.sistema.aluno.infrastructure.security;

import com.sistema.aluno.application.exception.AcessoNegadoException;
import com.sistema.aluno.application.ports.out.UsuarioLogadoPort;
import com.sistema.aluno.application.security.Perfil;
import com.sistema.aluno.application.security.UsuarioLogado;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/** Lê o usuário do JWT validado pelo Spring Security. */
@Component
public class JwtUsuarioLogadoAdapter implements UsuarioLogadoPort {

    @Override
    public UsuarioLogado atual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken token)) {
            throw new AcessoNegadoException("Requisição sem usuário autenticado.");
        }
        Jwt jwt = token.getToken();
        try {
            Perfil perfil = Perfil.valueOf(jwt.getClaimAsString("role"));
            return new UsuarioLogado(paraLong(jwt.getClaim("userId")), jwt.getSubject(), perfil, paraLong(jwt.getClaim("refId")));
        } catch (RuntimeException e) {
            throw new AcessoNegadoException("Token sem perfil válido.");
        }
    }

    /** Token bruto da requisição atual (para repassar a outros microsserviços). */
    public static String tokenAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth instanceof JwtAuthenticationToken token ? token.getToken().getTokenValue() : null;
    }

    private static Long paraLong(Object valor) {
        if (valor == null) return null;
        if (valor instanceof Number n) return n.longValue();
        return Long.valueOf(valor.toString());
    }
}
