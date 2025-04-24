package com.laterna.connexeauth.v1.usersession.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UserSessionDTO(
        Long id,
        String deviceInfo,
        String ipAddress,
        Instant lastActivity,
        Boolean isActive
) {
}
