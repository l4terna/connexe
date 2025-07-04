package com.laterna.connexemain.v1.message.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record CreateMessageDTO(
        String content,
        Long replyId,
        MultipartFile[] attachments
) {
}
