package com.laterna.connexemain.v1.hub.member;

import com.laterna.connexemain.v1.hub.member.dto.HubMemberDTO;
import com.laterna.connexemain.v1.user.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface HubMemberMapper {
    HubMemberDTO toDTO(HubMember hubMember);
}
