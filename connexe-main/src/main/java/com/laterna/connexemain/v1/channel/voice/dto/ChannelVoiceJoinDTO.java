package com.laterna.connexemain.v1.channel.voice.dto;

import com.laterna.connexemain.v1.user.dto.UserDTO;
import lombok.Builder;

@Builder
public record ChannelVoiceJoinDTO (
        Long channelId,
        UserDTO user
) {
}
