package com.laterna.connexemain.v1.hub.member.dto;

import com.laterna.connexemain.v1.role.dto.RoleDTO;
import com.laterna.connexemain.v1.user.dto.UserDTO;

import java.time.Instant;
import java.util.List;

public record HubMemberDTO(
        Long id,
        UserDTO user,
        Instant joinedAt,
        List<RoleDTO> roles,
        Boolean isOwner
) {
}
