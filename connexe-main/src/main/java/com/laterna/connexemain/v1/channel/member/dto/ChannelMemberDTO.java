package com.laterna.connexemain.v1.channel.member.dto;

import com.laterna.connexemain.v1.channel.dto.ChannelDTO;
import com.laterna.connexemain.v1.user.dto.UserDTO;

import java.time.Instant;

public record ChannelMemberDTO(
        Long id,
        ChannelDTO channel,
        UserDTO user,
        Instant joinedAt
) {
}
