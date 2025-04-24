package com.laterna.connexemain.v1.user.presence.tracking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompositePresenceTracker {
    private final List<AbstractTrackingService> trackers;

    public void updateUserPresence(Long userId) {
        trackers.forEach(tracker -> tracker.updateUserPresence(userId));
    }

    public void removeUserPresence(Long userId) {
        trackers.forEach(tracker -> tracker.removeUserPresence(userId));
    }
}
