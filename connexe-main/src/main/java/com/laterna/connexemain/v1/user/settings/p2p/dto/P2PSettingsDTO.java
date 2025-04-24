package com.laterna.connexemain.v1.user.settings.p2p.dto;

public record P2PSettingsDTO(
        Long targetUserId,
        Boolean isMuted,
        Integer volumeLevel
) {
}
