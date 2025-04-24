package com.laterna.connexemain.v1.user.presence.tracking;

import com.laterna.connexemain.v1.channel.ChannelService;
import com.laterna.connexemain.v1.user.presence.enumeration.Presence;
import com.laterna.connexemain.v1.user.presence.event.UserPresenceChangeChannelEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChannelTrackingService extends AbstractTrackingService {
    private final ChannelService channelService;
    private final ApplicationEventPublisher eventPublisher;

    private static final String DG_CHANNEL_ONLINE_MEMBERS = "dg_channel:%d:online_members";

    public ChannelTrackingService(RedisTemplate<String, Object> redisTemplate,
                                  ChannelService channelService,
                                  ApplicationEventPublisher eventPublisher) {
        super(redisTemplate);
        this.channelService = channelService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected String getKeyFormat() {
        return DG_CHANNEL_ONLINE_MEMBERS;
    }

    @Override
    protected Set<Long> getEntityIdsByUserId(Long userId) {
        return channelService.findAllChannelIdsByUserId(userId);
    }

    @Override
    protected void afterChange(Long userId, Long entityId, Presence presence) {
        eventPublisher.publishEvent(new UserPresenceChangeChannelEvent(userId, entityId, presence));
    }
}
