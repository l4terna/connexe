package com.laterna.connexemain.v1.channel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laterna.connexemain.v1.channel.enumeration.ChannelType;
import com.laterna.connexemain.v1.user.dto.UserDTO;

import java.util.List;

public record ChannelDTO (
        Long id,
        String name,
        Long categoryId,
        Integer position,
        ChannelType type,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<UserDTO> activeUsers
) {
}
