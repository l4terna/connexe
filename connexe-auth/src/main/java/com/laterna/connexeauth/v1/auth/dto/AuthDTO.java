package com.laterna.connexeauth.v1.auth.dto;

import lombok.Builder;
import com.laterna.connexeauth.v1.user.dto.UserDTO;

@Builder
public record AuthDTO(
        UserDTO user,
        String token
) {
}
