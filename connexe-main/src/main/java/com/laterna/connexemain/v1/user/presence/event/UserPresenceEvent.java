package com.laterna.connexemain.v1.user.presence.event;

public record UserPresenceEvent(
        Long userId,
        String fingerprint,
        boolean isConnected
) {
}
