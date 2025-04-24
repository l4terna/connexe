package com.laterna.connexemain.v1.channel.dto;

import jakarta.validation.constraints.Positive;

public record UpdateChannelDTO(
        String name,
        @Positive
        Long categoryId,
        @Positive
        Integer position
) {
}
