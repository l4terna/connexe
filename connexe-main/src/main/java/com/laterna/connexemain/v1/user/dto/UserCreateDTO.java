package com.laterna.connexemain.v1.user.dto;

import lombok.Builder;

@Builder
public record UserCreateDTO(
    String email,
    String login,
    Long userId
) {
}
