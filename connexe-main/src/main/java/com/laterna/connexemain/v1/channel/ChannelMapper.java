package com.laterna.connexemain.v1.channel;

import com.laterna.connexemain.v1.channel.dto.ChannelDTO;
import com.laterna.connexemain.v1.user.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChannelMapper {
    @Mapping(target = "activeUsers", ignore = true)
    ChannelDTO toDTO(Channel channel);

    @Mapping(target = "activeUsers", expression = "java(users)")
    ChannelDTO toDTO(Channel channel, List<UserDTO> users);
}
