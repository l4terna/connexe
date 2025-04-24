package com.laterna.connexemain.v1.hub.member.dto;

import com.laterna.connexemain.v1.user.dto.UserDTO;

import java.time.Instant;

public record HubMemberDTO(
        Long id,
        UserDTO user,
        Instant joinedAt
) {
}
