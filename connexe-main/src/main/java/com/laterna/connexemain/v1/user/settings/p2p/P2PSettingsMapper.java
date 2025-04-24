package com.laterna.connexemain.v1.user.settings.p2p;

import com.laterna.connexemain.v1.user.settings.p2p.dto.P2PSettingsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface P2PSettingsMapper {
    P2PSettingsDTO toDTO(P2PSettings entity);
}
