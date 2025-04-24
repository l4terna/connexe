package com.laterna.connexemain.v1.hub.invite.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record CreateInviteDTO(
        @Positive
        Integer maxUses,

        @Future
        @JsonFormat(pattern = "yyyy-MM-dd")
        OffsetDateTime expiresAt
) {
}
