package com.laterna.connexemain.v1.hub.mute.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record HubMuteDTO(
        @NotNull
        Long userId,
        Instant expiresAt
) {
}
