package com.laterna.connexeauth.v1.token.shared.dto;

import lombok.Builder;
import com.laterna.connexeauth.v1.user.User;
import com.laterna.connexeauth.v1.usersession.UserSession;

@Builder
public record CreateTokenDTO(
    UserSession userSession,
    User user,
    String token
) {
}
