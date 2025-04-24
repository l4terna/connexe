package com.laterna.connexemain.v1.hub.mute.dto;

import jakarta.validation.constraints.NotNull;

public record HubUnmuteDTO(
        @NotNull
        Long userId
) {
}
