package com.laterna.connexemain.v1.channel.member;

import com.laterna.connexemain.v1.channel.member.dto.ChannelMemberDTO;
import com.laterna.connexemain.v1.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ChannelMemberMapper {
    @Mapping(target = "channelId", source = "member.channel.id")
    ChannelMemberDTO toDTO(ChannelMember member);
}
