package com.laterna.connexemain.v1.channel.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateDirectChannelDTO(
        @NotEmpty
        List<Long> members
) {
}
