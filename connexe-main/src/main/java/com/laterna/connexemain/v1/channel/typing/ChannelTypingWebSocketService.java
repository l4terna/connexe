package com.laterna.connexemain.v1.channel.typing;

import com.laterna.connexemain.v1._shared.websocket.dto.WebSocketMessage;
import com.laterna.connexemain.v1._shared.websocket.enumeration.WebSocketMessageType;
import com.laterna.connexemain.v1.channel.typing.enumeration.ChannelTypingStatus;
import com.laterna.connexemain.v1.channel.typing.event.ChannelTypingStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ChannelTypingWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendChannelTypingChangedMessage(ChannelTypingStatusChangeEvent event) {
        WebSocketMessage.WebSocketMessageBuilder wsmb;

        if (event.channelTypingStatus() == ChannelTypingStatus.STARTED) {
            wsmb = WebSocketMessage.builder(WebSocketMessageType.TYPING_STARTED);
        } else {
            wsmb = WebSocketMessage.builder(WebSocketMessageType.TYPING_STOPPED);
        }

        WebSocketMessage wsm = wsmb
                .add("channelId", event.channelId())
                .add("user", event.user())
                .build();

        messagingTemplate.convertAndSend(
                "/v1/topic/channels/" + event.channelId() + "/typing",
                wsm
        );
    }
}
