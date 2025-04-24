package com.laterna.connexemain.v1.user.integration;

import com.google.protobuf.ServiceException;
import com.laterna.proto.v1.UserLastActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserSessionServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<UserLastActivityDTO> getLastActivity(Long userId) {
        return webClientBuilder.build()
                .post()
                .uri("lb://connexe-auth/api/v1/users/" + userId + "/last-activity")
                .header("X-User-Id", userId.toString())
                .retrieve()
                .bodyToMono(UserLastActivityDTO.class)
                .onErrorResume(error -> {
                    return Mono.error(new ServiceException("Error during get a product", error));
                });
    }
}
