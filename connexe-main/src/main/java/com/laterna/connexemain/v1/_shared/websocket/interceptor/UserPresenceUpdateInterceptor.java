package com.laterna.connexemain.v1._shared.websocket.interceptor;

import com.laterna.connexemain.v1.user.presence.event.UserPresenceEvent;
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
public class UserPresenceUpdateInterceptor implements ChannelInterceptor {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return ChannelInterceptor.super.preSend(message, channel);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Long userId = (Long) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");
            String fingerprint = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("__fprid");

            eventPublisher.publishEvent(new UserPresenceEvent(userId, fingerprint, true));

        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            Long userId = (Long) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");
            String fingerprint = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("__fprid");

            eventPublisher.publishEvent(new UserPresenceEvent(userId, fingerprint, false));
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }
}
