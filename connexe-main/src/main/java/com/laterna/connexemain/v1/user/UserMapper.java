package com.laterna.connexemain.v1.user;

import com.laterna.connexemain.v1._shared.integration.user.session.UserSessionServiceClient;
import com.laterna.connexemain.v1.user.dto.UserDTO;
import com.laterna.connexemain.v1.user.presence.UserPresenceService;
import com.laterna.connexemain.v1.user.presence.enumeration.Presence;
import com.laterna.connexemain.v1.user.settings.p2p.dto.P2PSettingsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    protected UserPresenceService userPresenceService;
    @Autowired
    private UserSessionServiceClient userSessionServiceClient;

    @Mapping(target = "presence", expression = "java(userPresenceService.getUserPresence(user.getId()))")
    @Mapping(target = "lastActivity", expression = "java(getLastActivity(user.getId()))")
    @Mapping(target = "p2pSettings", ignore = true)
    public abstract UserDTO toDTO(User user);

    @Mapping(target = "presence", expression = "java(userPresenceService.getUserPresence(user.getId()))")
    @Mapping(target = "lastActivity", expression = "java(getLastActivity(user.getId()))")
    @Mapping(target = "p2pSettings", expression = "java(p2pSettingsDTO)")
    public abstract UserDTO toDTO(User user, P2PSettingsDTO p2pSettingsDTO);

    Instant getLastActivity(Long userId) {
        if (userPresenceService.getUserPresence(userId) == Presence.ONLINE) {
            return null;
        }

        return Instant.ofEpochMilli(userSessionServiceClient.getLastActivity(userId).getLastActivityTimestamp());
    }
}