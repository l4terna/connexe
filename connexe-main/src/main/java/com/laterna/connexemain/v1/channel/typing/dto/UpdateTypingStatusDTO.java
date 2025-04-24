package com.laterna.connexemain.v1.channel.typing.dto;

import com.laterna.connexemain.v1.channel.typing.enumeration.ChannelTypingStatus;

public record UpdateTypingStatusDTO(
        ChannelTypingStatus typingStatus
) {
}
