package com.laterna.connexemain.v1.user.presence.listener;

import com.laterna.connexemain.v1._shared.websocket.dto.WebSocketMessage;
import com.laterna.connexemain.v1._shared.websocket.enumeration.WebSocketMessageType;
import com.laterna.connexemain.v1.user.UserService;
import com.laterna.connexemain.v1.user.dto.UserDTO;
import com.laterna.connexemain.v1.user.presence.event.UserPresenceChangeChannelEvent;
import com.laterna.connexemain.v1.user.presence.event.UserPresenceChangeHubEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPresenceChangeListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    @EventListener
    public void handlePresenceChangeHubEvent(UserPresenceChangeHubEvent event) {
        UserDTO user = userService.findById(event.userId());

        WebSocketMessage wsm = WebSocketMessage.builder(WebSocketMessageType.USER_PRESENCE_CHANGE)
                .add("user", user)
                .build();

        messagingTemplate.convertAndSend(
                "/v1/topic/hubs/" + event.hubId() + "/members",
                wsm
        );
    }

    @EventListener
    public void handlePresenceChangeChannelEvent(UserPresenceChangeChannelEvent event) {
        UserDTO user = userService.findById(event.userId());

        WebSocketMessage wsm = WebSocketMessage.builder(WebSocketMessageType.USER_STATUS_CHANGE)
                .add("user", user)
                .build();

        messagingTemplate.convertAndSend(
                "/v1/topic/channels/" + event.channelId() + "/members",
                wsm
        );
    }
}
