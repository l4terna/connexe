package com.laterna.connexemain.v1.channel.voice;

import com.laterna.connexemain.v1.channel.Channel;
import com.laterna.connexemain.v1.channel.ChannelService;
import com.laterna.connexemain.v1.channel.voice.dto.ChannelVoiceDTO;
import com.laterna.connexemain.v1.channel.voice.dto.ChannelVoiceJoinDTO;
import com.laterna.connexemain.v1.user.User;
import com.laterna.connexemain.v1.user.UserMapper;
import com.laterna.connexemain.v1.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelVoiceService {
    public static final String CHANNEL_VOICE_ACTIVE_KEY = "channel:%s:voice:active";
    public static final String USER_VOICE_ACTIVE_KEY = "user:%s:voice:active";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelService channelService;
    private final UserService userService;
    private final ChannelVoiceWebSocketService channelVoiceWebSocketService;
    private final UserMapper userMapper;

    public void addUserToActiveChannel(ChannelVoiceDTO voiceDTO) {
        Channel channel = channelService.findChannelById(voiceDTO.channelId());
        User user = userService.findUserById(voiceDTO.userId());

        String userVoiceActiveKey = String.format(USER_VOICE_ACTIVE_KEY, user.getId());
        String channelVoiceActiveKey = String.format(CHANNEL_VOICE_ACTIVE_KEY, channel.getId());

        removeUserActiveChannel(user.getId());

        redisTemplate.opsForSet().add(channelVoiceActiveKey, user.getId());
        redisTemplate.opsForValue().set(userVoiceActiveKey, channel.getId());

        channelVoiceWebSocketService.sendMessageNewToActiveChannel(
                ChannelVoiceJoinDTO.builder()
                        .channelId(channel.getId())
                        .user(userMapper.toDTO(user))
                        .build()
        );
    }

    public void removeUserActiveChannel(Long userId) {
        User user = userService.findUserById(userId);
        String userVoiceActiveKey = String.format(USER_VOICE_ACTIVE_KEY, user.getId());

        Optional.ofNullable(redisTemplate.opsForValue().getAndDelete(userVoiceActiveKey))
                .ifPresent(value -> {
                    redisTemplate.opsForSet().remove(String.format(CHANNEL_VOICE_ACTIVE_KEY, value), userId.toString());

                    channelVoiceWebSocketService.sendMessageLeftToActiveChannel(
                            ChannelVoiceDTO.builder()
                                    .userId(userId)
                                    .channelId(Long.parseLong(value.toString()))
                                    .build()
                    );
                });
    }

    public Set<Long> getUserIdsByChannelId(Long channelId) {
        String channelVoiceActiveKey = String.format(CHANNEL_VOICE_ACTIVE_KEY, channelId);

        Set<Object> rawActives = redisTemplate.opsForSet().members(channelVoiceActiveKey);

        if (rawActives != null && !rawActives.isEmpty()) {
            return rawActives.stream()
                    .map(obj -> Long.parseLong(obj.toString()))
                    .collect(Collectors.toSet());
        }

        return new HashSet<>();
    }
}
