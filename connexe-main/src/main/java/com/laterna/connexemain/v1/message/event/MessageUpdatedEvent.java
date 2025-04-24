package com.laterna.connexemain.v1.message.event;

import com.laterna.connexemain.v1.message.dto.MessageDTO;

public record MessageUpdatedEvent(
        MessageDTO message,
        Long channelId
) {
}
