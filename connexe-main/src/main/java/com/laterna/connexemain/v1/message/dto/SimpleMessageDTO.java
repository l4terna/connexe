package com.laterna.connexemain.v1.message.dto;

import com.laterna.connexemain.v1.user.dto.UserDTO;

public record SimpleMessageDTO (
        Long id,
        String content,
        Integer attachmentsCount,
        UserDTO author,
        Long channelId
) {
}
