package com.laterna.connexemain.v1.channel.dto;

import com.laterna.connexemain.v1.channel.enumeration.ChannelType;
import jakarta.validation.constraints.*;

public record CreateHubChannelDTO(
        @NotBlank @Size(max = 255) String name,
        @Positive Long categoryId,
        ChannelType type
) {
}
