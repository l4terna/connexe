package com.laterna.connexemain.v1.channel.member;

import com.laterna.connexemain.v1.channel.ChannelMapper;
import com.laterna.connexemain.v1.channel.member.dto.ChannelMemberDTO;
import com.laterna.connexemain.v1.user.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ChannelMapper.class})
public interface ChannelMemberMapper {
    ChannelMemberDTO toDTO(ChannelMember save);
}
