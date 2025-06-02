package com.laterna.connexemain.v1.message.dto;

import com.laterna.connexemain.v1.user.dto.UserDTO;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record MessageDTO(
        Long id,
        String content,
        SimpleMessageDTO reply,
        Instant createdAt,
        Instant lastModifiedAt,
        UserDTO author,
        Long channelId,
        Integer status,
        Long readByCount, // only for GROUP_DC, TEXT channels,
        List<String> attachments
) {
}
