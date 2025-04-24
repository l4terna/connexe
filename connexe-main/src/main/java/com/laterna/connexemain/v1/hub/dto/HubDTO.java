package com.laterna.connexemain.v1.hub.dto;

import com.laterna.connexemain.v1.hub.enumeration.HubType;
import com.laterna.connexemain.v1.user.dto.UserDTO;

import java.time.Instant;

public record HubDTO(
        Long id,
        UserDTO owner,
        String name,
        HubType type,
        String avatar,
        Instant createdAt
) {
}
