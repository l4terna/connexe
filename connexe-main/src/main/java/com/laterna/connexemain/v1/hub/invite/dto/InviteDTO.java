package com.laterna.connexemain.v1.hub.invite.dto;

import java.time.OffsetDateTime;

public record InviteDTO (
        Long id,
        String code,
        Integer maxUses,
        Integer currentUses,
        Boolean isActive,
        OffsetDateTime expiresAt
) {
}
