package com.laterna.connexemain.v1.channel.voice.dto;

import lombok.Builder;

@Builder
public record ChannelVoiceDTO(
        Long userId,
        Long channelId
){
}
