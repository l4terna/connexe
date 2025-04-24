package com.laterna.connexemain.v1.user.presence.tracking;

import com.laterna.connexemain.v1.user.presence.enumeration.Presence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractTrackingService {
    protected final RedisTemplate<String, Object> redisTemplate;
    
    protected abstract String getKeyFormat();
    protected abstract Set<Long> getEntityIdsByUserId(Long userId);
    protected abstract void afterChange(Long userId, Long entityId, Presence presence);

    public void updateUserPresence(Long userId) {
        Set<Long> entityIds = getEntityIdsByUserId(userId);
        
        entityIds.forEach(entityId -> {
            String key = String.format(getKeyFormat(), entityId);
            redisTemplate.opsForSet().add(key, userId.toString());

            afterChange(userId, entityId, Presence.ONLINE);
        });
    }
    
    public Set<Long> getAllOnlineUserIds(Long entityId) {
        String key = String.format(getKeyFormat(), entityId);
        Set<Object> userIds = redisTemplate.opsForSet().members(key);
        
        if (userIds == null || userIds.isEmpty()) return null;
        
        return userIds.stream()
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(Collectors.toSet());
    }
    
    public void removeUserPresence(Long userId) {
        Set<Long> entityIds = getEntityIdsByUserId(userId);
        
        entityIds.forEach(entityId -> {
            String key = String.format(getKeyFormat(), entityId);

            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId.toString()))) {
                redisTemplate.opsForSet().remove(key, userId.toString());

                if (Objects.equals(redisTemplate.opsForSet().size(key), 0L)) {
                    redisTemplate.delete(key);
                }

                afterChange(userId, entityId, Presence.OFFLINE);
            }
        });
    }

}