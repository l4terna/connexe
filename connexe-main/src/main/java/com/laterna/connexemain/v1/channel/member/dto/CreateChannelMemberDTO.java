package com.laterna.connexemain.v1.channel.member.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateChannelMemberDTO(
        @NotEmpty
        List<Long> users
) {
}
