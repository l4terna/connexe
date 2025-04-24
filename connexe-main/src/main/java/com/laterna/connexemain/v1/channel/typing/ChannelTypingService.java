package com.laterna.connexemain.v1.channel.typing;

import com.laterna.connexemain.v1.channel.typing.dto.UpdateTypingStatusDTO;
import com.laterna.connexemain.v1.channel.typing.enumeration.ChannelTypingStatus;
import com.laterna.connexemain.v1.channel.typing.event.ChannelTypingStatusChangeEvent;
import com.laterna.connexemain.v1.user.UserService;
import com.laterna.connexemain.v1.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChannelTypingService {
    private static final String CHANNEL_TYPING_KEY = "channel:%s:typing:%s";
    private static final long TYPING_TIMEOUT_SECONDS = 5;

    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService userService;

    public void updateTypingIndicator(
            UpdateTypingStatusDTO updateTypingStatusDTO,
            Long channelId,
            Principal principal) {
        UserDTO user = userService.findByEmail(principal.getName());
        String key = String.format(CHANNEL_TYPING_KEY, channelId, user.id());

        if (updateTypingStatusDTO.typingStatus() == ChannelTypingStatus.STARTED) {
            redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()),
                    Duration.ofSeconds(TYPING_TIMEOUT_SECONDS));
        } else {
            redisTemplate.delete(key);
        }

        eventPublisher.publishEvent(new ChannelTypingStatusChangeEvent(user, channelId, updateTypingStatusDTO.typingStatus()));
    }

    public List<UserDTO> getChannelTypingUsers(Long channelId) {
        Set<Long> typingUserIds = getChannelTypingUserIds(channelId);

        return userService.findAllByIds(typingUserIds);
    }

    private Set<Long> getChannelTypingUserIds(Long channelId) {
        String pattern = String.format(CHANNEL_TYPING_KEY, channelId, "*");
        Set<String> keys = redisTemplate.keys(pattern);

        Set<Long> userIds = new HashSet<>();
        for (String key : keys) {
            String[] parts = String.valueOf(key).split(":");

            if (parts.length == 4) {
                userIds.add(Long.parseLong(parts[3]));
            }
        }

        return userIds;
    }
}
