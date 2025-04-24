package com.laterna.connexemain.v1.message.event;

import com.laterna.connexemain.v1.message.dto.MessageDTO;


public record MessageCreatedEvent(
        MessageDTO message,
        Long channelId
) {
}
