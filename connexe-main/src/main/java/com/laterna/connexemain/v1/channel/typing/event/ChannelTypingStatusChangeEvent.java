package com.laterna.connexemain.v1.channel.typing.event;

import com.laterna.connexemain.v1.channel.typing.enumeration.ChannelTypingStatus;
import com.laterna.connexemain.v1.user.dto.UserDTO;

public record ChannelTypingStatusChangeEvent(
        UserDTO user,
        Long channelId,
        ChannelTypingStatus channelTypingStatus
) {
}
