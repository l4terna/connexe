package com.laterna.connexemain.v1.channel.typing.listener;

import com.laterna.connexemain.v1.channel.typing.enumeration.ChannelTypingStatus;
import com.laterna.connexemain.v1.channel.typing.event.ChannelTypingStatusChangeEvent;
import com.laterna.connexemain.v1.user.UserService;
import com.laterna.connexemain.v1.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelTypingExpirationListener implements MessageListener {
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());

        if (expiredKey.startsWith("channel:") && expiredKey.contains(":typing:")) {
            String[] parts = expiredKey.split(":");

            if (parts.length == 4) {
                Long channelId = Long.valueOf(parts[1]);
                Long userId = Long.valueOf(parts[3]);

                UserDTO user = userService.findById(userId);

                eventPublisher.publishEvent(new ChannelTypingStatusChangeEvent(user, channelId, ChannelTypingStatus.STOPPED));
            }
        }
    }
}
