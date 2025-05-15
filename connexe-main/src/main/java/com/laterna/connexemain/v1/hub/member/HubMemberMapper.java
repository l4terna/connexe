package com.laterna.connexemain.v1.hub.member;

import com.laterna.connexemain.v1.hub.member.dto.HubMemberDTO;
import com.laterna.connexemain.v1.role.RoleMapper;
import com.laterna.connexemain.v1.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, RoleMapper.class})
public interface HubMemberMapper {
    @Mapping(target = "isOwner", expression = "java(isOwner(hubMember))")
    HubMemberDTO toDTO(HubMember hubMember);

    default Boolean isOwner(HubMember hubMember) {
        return hubMember.getHub().getOwner().getId().equals(hubMember.getUser().getId());
    }
}
