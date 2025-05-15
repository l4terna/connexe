package com.laterna.connexemain.v1.hub.invite.dto;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record CreateInviteDTO(
        @Positive
        Integer maxUses,

        @Future
        Instant expiresAt
) {
}
