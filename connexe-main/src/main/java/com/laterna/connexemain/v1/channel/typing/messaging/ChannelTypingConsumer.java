package com.laterna.connexemain.v1.channel.typing.messaging;

import com.laterna.connexemain.v1.channel.typing.ChannelTypingWebSocketService;
import com.laterna.connexemain.v1.channel.typing.event.ChannelTypingStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelTypingConsumer {
    private final ChannelTypingWebSocketService channelTypingWebSocketService;

    @KafkaListener(topics = "channel.typing", groupId = "channel-service.typing-processor.typing")
    public void handleTypingStatus(ChannelTypingStatusChangeEvent event) {
        channelTypingWebSocketService.sendChannelTypingChangedMessage(event);
    }
}
