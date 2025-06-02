package com.laterna.connexemain.v1.channel.member.dto;

import com.laterna.connexemain.v1.user.dto.UserDTO;

import java.time.Instant;

public record ChannelMemberDTO(
        Long id,
        Long channelId,
        UserDTO user,
        Instant joinedAt
) {
}
