package com.laterna.connexemain.v1._shared.integration.user.session;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ServiceException;
import com.google.protobuf.util.JsonFormat;
import com.laterna.proto.v1.UserLastActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserSessionServiceClient {

    private final WebClient.Builder webClientBuilder;

    public UserLastActivityDTO getLastActivity(Long userId) {
        return webClientBuilder
                .build()
                .get()
                .uri("lb://connexe-auth/api/v1/users/" + userId + "/last-activity")
                .retrieve()
                .bodyToMono(String.class)
                .map(jsonString -> {
                    try {
                        UserLastActivityDTO.Builder builder = UserLastActivityDTO.newBuilder();
                        JsonFormat.parser().merge(jsonString, builder);
                        return builder.build();
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onErrorResume(error -> {
                    return Mono.error(new ServiceException("Error during get an activity", error));
                })
                .block();
    }

    public Long getUserIdByFingerprint(String fingerprint, Long userId) {
        return webClientBuilder
                .build()
                .get()
                .uri("lb://connexe-auth/api/v1/users/" + userId)
                .cookie("__fprid", fingerprint)
                .retrieve()
                .bodyToMono(Long.class)
                .onErrorResume(error -> {
                    return Mono.error(new ServiceException("Error during get an user id", error));
                })
                .block();
    }
}
