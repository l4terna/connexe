package com.laterna.connexemain.v1.channel.dto;

import com.laterna.connexemain.v1.channel.enumeration.ChannelType;
import com.laterna.connexemain.v1.user.dto.UserDTO;

import java.util.List;

public record DirectChannelDTO(
        Long id,
        ChannelType type,

        // участники direct чата
        List<UserDTO> members
){ }
