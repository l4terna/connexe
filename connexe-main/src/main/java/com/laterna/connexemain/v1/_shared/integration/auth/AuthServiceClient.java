package com.laterna.connexemain.v1._shared.integration.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class AuthServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Long validateToken(String authHeader, String  fingerprint) {
        try {
            return webClientBuilder.build()
                .post()
                .uri("lb://connexe-auth/api/v1/auth/validate-token")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .cookies(cookies -> {
                    cookies.add("__fprid", fingerprint);
                })
                .retrieve()
                .bodyToMono(Long.class)
                .onErrorResume(e ->  Mono.error(new AccessDeniedException("Access denied")))
                .block();
        } catch (Exception e) {
            return null;
        }
    }
}
