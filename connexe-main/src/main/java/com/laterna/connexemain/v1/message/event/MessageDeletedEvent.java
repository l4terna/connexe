package com.laterna.connexemain.v1.message.event;


public record MessageDeletedEvent(
        Long messageId,
        Long channelId
) {
}
