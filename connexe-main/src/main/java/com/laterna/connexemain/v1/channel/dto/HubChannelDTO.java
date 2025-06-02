package com.laterna.connexemain.v1.channel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laterna.connexemain.v1.channel.enumeration.ChannelType;
import com.laterna.connexemain.v1.user.dto.UserDTO;

import java.util.List;

public record HubChannelDTO(
        Long id,
        String name,
        Long categoryId,
        Integer position,
        ChannelType type,

        // получаем активных юзеров, чтобы определить настройки каждого из них у текущего юзера.
        // (в муте он или нет, уровень звука для текущего и тд)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<UserDTO> activeUsers
) {
}
