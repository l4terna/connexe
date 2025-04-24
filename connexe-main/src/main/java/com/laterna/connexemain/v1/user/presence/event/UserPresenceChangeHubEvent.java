package com.laterna.connexemain.v1.user.presence.event;

import com.laterna.connexemain.v1.user.presence.enumeration.Presence;

public record UserPresenceChangeHubEvent(
        Long userId,
        Long hubId,
        Presence presence
) {
}
