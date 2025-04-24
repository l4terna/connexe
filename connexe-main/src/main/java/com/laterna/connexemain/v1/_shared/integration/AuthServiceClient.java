package com.laterna.connexemain.v1._shared.integration;

import com.google.protobuf.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<Long> validateToken(String authHeader) {
        return webClientBuilder.build()
            .post()
            .uri("lb://connexe-auth/api/v1/auth/validate-token")
            .header(HttpHeaders.AUTHORIZATION, authHeader)
            .retrieve()
            .bodyToMono(Long.class)
            .onErrorResume(error -> {
                return Mono.error(new ServiceException("Error during validate", error));
            });
    }
}
