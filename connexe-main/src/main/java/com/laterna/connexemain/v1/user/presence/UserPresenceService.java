package com.laterna.connexemain.v1.user.presence;

import com.laterna.connexemain.v1.user.presence.enumeration.Presence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPresenceService {
    private final RedisTemplate<String, Object> redisTemplate;

    public Presence getUserPresence(Long userId) {
        String userKey = String.format(UserPresenceManageService.USER_PRESENCE_KEY, userId);
        Object fingerprint = redisTemplate.opsForValue().get(userKey);

        if (fingerprint != null) {
            String userDeviceKey = String.format(UserPresenceManageService.USER_DEVICE_KEY, userId, fingerprint);
            Object presence = redisTemplate.opsForValue().get(userDeviceKey);

            if (presence != null) {
                return Presence.valueOf(presence.toString());
            }
        }

        return Presence.OFFLINE;
    }
}
