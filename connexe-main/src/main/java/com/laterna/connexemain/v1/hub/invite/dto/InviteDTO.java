package com.laterna.connexemain.v1.hub.invite.dto;

import java.time.Instant;

public record InviteDTO (
        Long id,
        String code,
        Integer maxUses,
        Integer currentUses,
        Boolean isActive,
        Instant expiresAt,
        Instant createdAt
) {
}
