package com.laterna.connexemain.v1.user.presence;

import com.laterna.connexemain.v1.user.presence.enumeration.Presence;
import com.laterna.connexemain.v1.user.presence.event.UserPresenceEvent;
import com.laterna.connexemain.v1.user.presence.tracking.CompositePresenceTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPresenceManageService {
    private final RedisTemplate<String, Object> redisTemplate;
    public static final String USER_DEVICE_KEY = "user:%s:fingerprint:%s";
    public static final String USER_PRESENCE_KEY = "user:%s:presence";
    private final CompositePresenceTracker compositePresenceTracker;


    @EventListener
    public void handleUserPresenceEvent(UserPresenceEvent event) {
        if (event.isConnected()) {
            update(event.userId(), event.fingerprint());
        } else {
            delete(event.userId(), event.fingerprint());
        }
    }

    public void update(Long userId, String fingerprint) {
        String userDevicePresenceKey = String.format(USER_DEVICE_KEY, userId, fingerprint);
        String userPresenceKey = String.format(USER_PRESENCE_KEY, userId);

        redisTemplate.opsForValue().set(userDevicePresenceKey, Presence.ONLINE);

        redisTemplate.opsForValue().set(userPresenceKey, fingerprint);

        compositePresenceTracker.updateUserPresence(userId);
    }

    public void delete(Long userId, String fingerprint) {
        String userDevicePresenceKey = String.format(USER_DEVICE_KEY, userId, fingerprint);
        String userPresenceKey = String.format(USER_PRESENCE_KEY, userId);

        redisTemplate.delete(userDevicePresenceKey);
        redisTemplate.delete(userPresenceKey);

        compositePresenceTracker.removeUserPresence(userId);
    }
}
