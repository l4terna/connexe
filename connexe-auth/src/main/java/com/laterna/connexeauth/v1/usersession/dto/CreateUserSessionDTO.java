package com.laterna.connexeauth.v1.usersession.dto;

import lombok.Builder;
import com.laterna.connexeauth.v1.user.User;

@Builder
public record CreateUserSessionDTO(
        User user
) {
}
