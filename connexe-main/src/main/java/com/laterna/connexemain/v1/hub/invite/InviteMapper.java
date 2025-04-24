package com.laterna.connexemain.v1.hub.invite;

import com.laterna.connexemain.v1.hub.invite.dto.InviteDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InviteMapper {
    InviteDTO toDTO(Invite invite);
}
