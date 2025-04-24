package com.laterna.connexemain.v1.user.presence.tracking;

import com.laterna.connexemain.v1.hub.HubService;
import com.laterna.connexemain.v1.user.presence.enumeration.Presence;
import com.laterna.connexemain.v1.user.presence.event.UserPresenceChangeHubEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class HubTrackingService extends AbstractTrackingService {
    private static final String HUB_ONLINE_MEMBERS = "hub:%d:online_members";
    private final HubService hubService;
    private final ApplicationEventPublisher eventPublisher;

    public HubTrackingService(RedisTemplate<String, Object> redisTemplate,
                              HubService hubService,
                              ApplicationEventPublisher eventPublisher) {
        super(redisTemplate);
        this.hubService = hubService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected String getKeyFormat() {
        return HUB_ONLINE_MEMBERS;
    }

    @Override
    protected Set<Long> getEntityIdsByUserId(Long userId) {
        return hubService.findAllHubIdsByUserId(userId);
    }

    @Override
    protected void afterChange(Long userId, Long entityId, Presence presence) {
        eventPublisher.publishEvent(new UserPresenceChangeHubEvent(userId, entityId, presence));
    }

}
