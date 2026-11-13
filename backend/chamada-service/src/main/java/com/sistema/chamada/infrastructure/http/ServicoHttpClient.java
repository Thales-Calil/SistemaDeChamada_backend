package com.sistema.chamada.infrastructure.http;

import com.sistema.chamada.application.exception.AcessoNegadoException;
import com.sistema.chamada.application.exception.NaoEncontradoException;
import com.sistema.chamada.application.exception.ServicoExternoException;
import com.sistema.chamada.infrastructure.security.JwtUsuarioLogadoAdapter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

/**
 * Cliente HTTP para chamar outro microsserviço repassando o JWT do usuário (token relay).
 * Converte as respostas de erro em exceções da aplicação (404, 403, 502) com mensagem clara,
 * para não virar um "500 genérico".
 */
public class ServicoHttpClient {

    private final String nomeServico;
    private final String baseUrl;
    private final RestClient restClient;

    public ServicoHttpClient(String nomeServico, String baseUrl) {
        this.nomeServico = nomeServico;
        this.baseUrl = baseUrl;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(15));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .requestInterceptor((request, body, execution) -> {
                    String token = JwtUsuarioLogadoAdapter.tokenAtual();
                    if (token != null) {
                        request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    public <T> T get(String uri, Class<T> tipo, Object... variaveis) {
        try {
            return restClient.get().uri(uri, variaveis).retrieve().body(tipo);
        } catch (RuntimeException e) {
            throw traduzir(e, uri);
        }
    }

    public <T> T get(String uri, ParameterizedTypeReference<T> tipo, Object... variaveis) {
        try {
            return restClient.get().uri(uri, variaveis).retrieve().body(tipo);
        } catch (RuntimeException e) {
            throw traduzir(e, uri);
        }
    }

    private RuntimeException traduzir(RuntimeException e, String uri) {
        if (e instanceof HttpClientErrorException.NotFound) {
            return new NaoEncontradoException(extrairMensagem((HttpClientErrorException) e,
                    "Recurso não encontrado no " + nomeServico + " (" + uri + ")."));
        }
        if (e instanceof HttpClientErrorException.Forbidden) {
            return new AcessoNegadoException(extrairMensagem((HttpClientErrorException) e,
                    "Acesso negado pelo " + nomeServico + "."));
        }
        if (e instanceof HttpClientErrorException.Unauthorized) {
            return new AcessoNegadoException("O " + nomeServico + " recusou o token (confira se o JWT_SECRET é o mesmo em todos os serviços).");
        }
        if (e instanceof RestClientException) {
            return new ServicoExternoException("Falha ao chamar o " + nomeServico + " em " + baseUrl + uri + ": " + e.getMessage(), e);
        }
        return e;
    }

    private static String extrairMensagem(HttpClientErrorException e, String padrao) {
        String corpo = e.getResponseBodyAsString();
        int i = corpo.indexOf("\"mensagem\":\"");
        if (i >= 0) {
            int inicio = i + 12;
            int fim = corpo.indexOf('"', inicio);
            if (fim > inicio) return corpo.substring(inicio, fim);
        }
        return padrao;
    }
}
