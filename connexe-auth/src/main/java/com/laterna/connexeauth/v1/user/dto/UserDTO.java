package com.laterna.connexeauth.v1.user.dto;


import java.time.Instant;

public record UserDTO(
        Long id,
        String login,
        String email,
        Instant createdAt
) {
}
