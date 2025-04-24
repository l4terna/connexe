package com.laterna.connexemain.v1.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laterna.connexemain.v1.user.presence.enumeration.Presence;
import com.laterna.connexemain.v1.user.settings.p2p.dto.P2PSettingsDTO;

import java.time.Instant;

public record UserDTO(
        Long id,
        String login,
        String email,
        Instant createdAt,
        Instant lastActivity,
        Presence presence,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        P2PSettingsDTO p2pSettings
) {
}
