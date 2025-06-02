package com.laterna.connexemain.v1.channel;

import com.laterna.connexemain.v1.channel.dto.DirectChannelDTO;
import com.laterna.connexemain.v1.channel.dto.HubChannelDTO;
import com.laterna.connexemain.v1.channel.member.ChannelMemberMapper;
import com.laterna.connexemain.v1.user.UserMapper;
import com.laterna.connexemain.v1.user.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ChannelMemberMapper.class})
public abstract class ChannelMapper {
    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "activeUsers", ignore = true)
    public abstract HubChannelDTO toHubDTO(Channel channel);

    @Mapping(target = "activeUsers", expression = "java(users)")
    public abstract HubChannelDTO toHubDTO(Channel channel, List<UserDTO> users);

    @Mapping(target = "members", expression = "java(filterMembers(channel, currentUserId))")
    public abstract DirectChannelDTO toDirectDTO(Channel channel, Long currentUserId);

    List<UserDTO> filterMembers(Channel channel, Long currentUserId) {
        if (channel.getMembers() == null) return Collections.emptyList();

        return channel.getMembers().stream()
                .map(member -> userMapper.toDTO(member.getUser()))
                .filter(user -> !user.id().equals(currentUserId))
                .toList();
    }
}
