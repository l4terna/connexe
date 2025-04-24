package com.laterna.connexemain.v1._shared.websocket.interceptor;

import com.laterna.connexemain.v1.channel.voice.event.ChannelVoiceDisconnectEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ChannelActiveUpdateInterceptor implements ChannelInterceptor {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return ChannelInterceptor.super.preSend(message, channel);

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            Long userId = (Long) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");

            applicationEventPublisher.publishEvent(new ChannelVoiceDisconnectEvent(userId));
        }

        return ChannelInterceptor.super.preSend(message, channel);

    }
}
