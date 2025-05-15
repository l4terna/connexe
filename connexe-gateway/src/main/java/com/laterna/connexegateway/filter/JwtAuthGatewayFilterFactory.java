package com.laterna.connexegateway.filter;

import com.google.protobuf.util.JsonFormat;
import com.laterna.proto.v1.ErrorResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {
    private final WebClient.Builder webClientBuilder;

    public JwtAuthGatewayFilterFactory(WebClient.Builder clientBuilder) {
        super(Config.class);
        this.webClientBuilder = clientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!isExcludedPath(request.getURI().getPath(), config.getExcludedPaths())) {
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange);
                }

                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return onError(exchange);
                }

                return webClientBuilder.build()
                        .post()
                        .uri("lb://connexe-auth/api/v1/auth/validate-token")
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .cookies(cookies -> {
                            request.getCookies().forEach((key, values) -> {
                                values.forEach(cookie -> {
                                   cookies.add(cookie.getName(), cookie.getValue());
                                });
                            });
                        })
                        .retrieve()
                        .bodyToMono(Long.class)
                        .map(userId -> {
                            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                    .header("X-User-Id", userId.toString())
                                    .build();

                            return exchange.mutate().request(modifiedRequest).build();
                        })
                        .flatMap(chain::filter)
                        .onErrorResume(error -> {
                            log.error(error.getMessage(), error);
                            return onError(exchange);
                        });
            }

            return chain.filter(exchange);
        };
    }

    private boolean isExcludedPath(String path, List<String> excludedPaths) {
        return excludedPaths.stream()
                .anyMatch(pattern -> {
                    if (pattern.endsWith("/**")) {
                        String prefix = pattern.substring(0, pattern.length() - 3);
                        return path.startsWith(prefix);
                    }
                    return path.equals(pattern);
                });
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse error = ErrorResponse.newBuilder()
                .setStatusCode(401)
                .setTimestamp(Instant.now().toEpochMilli())
                .setPath(exchange.getRequest().getURI().getPath())
                .setType(HttpStatus.UNAUTHORIZED.name())
                .setMessage("Invalid authorization").build();

        JsonFormat.Printer printer = JsonFormat.printer();

        byte[] errorBytes;

        try {
            String jsonError = printer.preservingProtoFieldNames().print(error);
            errorBytes = jsonError.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("JSON format error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }

        return response.writeWith(Mono.just(response.bufferFactory()
                .wrap(errorBytes)));
    }

    public static class Config {
        @Getter
        private final List<String> excludedPaths = new ArrayList<>();
    }
}
