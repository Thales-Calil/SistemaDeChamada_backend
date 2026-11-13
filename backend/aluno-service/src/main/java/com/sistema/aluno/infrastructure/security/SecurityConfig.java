package com.sistema.aluno.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Segurança do microsserviço (defesa em profundidade): toda requisição precisa do JWT
 * emitido pelo API Gateway (mesmo JWT_SECRET). Assim, mesmo chamando a URL
 * *.azurewebsites.net direto, sem passar pelo Gateway, ninguém acessa os dados sem token válido.
 */
@Configuration
public class SecurityConfig {

    public static final String ISSUER = "sistema-chamadas-api-gateway";

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET ausente ou com menos de 32 caracteres (use o mesmo valor do API Gateway).");
        }
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(ISSUER));
        return decoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable()) // o navegador fala só com o Gateway; CORS fica lá
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(token -> new JwtAuthenticationToken(token,
                                        List.of(new SimpleGrantedAuthority("ROLE_" + token.getClaimAsString("role"))))))
                        .authenticationEntryPoint((request, response, ex) ->
                                escreverErro(response, HttpStatus.UNAUTHORIZED, "Token ausente, inválido ou expirado.")))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) ->
                                escreverErro(response, HttpStatus.UNAUTHORIZED, "Token ausente, inválido ou expirado."))
                        .accessDeniedHandler((request, response, e) ->
                                escreverErro(response, HttpStatus.FORBIDDEN, "Seu perfil não tem permissão para esta operação.")));
        return http.build();
    }

    private static void escreverErro(jakarta.servlet.http.HttpServletResponse response, HttpStatus status, String mensagem)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"status\":" + status.value() + ",\"erro\":\"" + status.getReasonPhrase()
                + "\",\"mensagem\":\"" + mensagem + "\",\"servico\":\"aluno-service\"}");
    }
}
