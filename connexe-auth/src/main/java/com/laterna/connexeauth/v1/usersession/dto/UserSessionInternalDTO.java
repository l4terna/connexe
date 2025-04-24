package com.laterna.connexeauth.v1.usersession.dto;

import lombok.Builder;
import com.laterna.connexeauth.v1.user.User;

import java.time.Instant;

@Builder
public record UserSessionInternalDTO(
    Long id,
    User user,
    String deviceInfo,
    String ipAddress,
    String fingerprint,
    Instant lastActivity
) {}