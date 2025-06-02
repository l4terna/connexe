package com.laterna.connexemain.v1.media.sign.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record MediaSignDTO(
        String sign,
        Instant expiresAt,
        Long userId
) {
}
