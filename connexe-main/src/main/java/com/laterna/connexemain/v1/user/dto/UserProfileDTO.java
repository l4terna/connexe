package com.laterna.connexemain.v1.user.dto;

import com.laterna.connexemain.v1.hub.member.dto.HubMemberDTO;
import lombok.Builder;

@Builder
public record UserProfileDTO(
        UserDTO user,
        HubMemberDTO hubMember
) {
}
