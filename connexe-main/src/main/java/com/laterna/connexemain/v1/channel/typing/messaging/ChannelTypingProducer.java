package com.laterna.connexemain.v1.channel.typing.messaging;

import com.laterna.connexemain.v1.channel.typing.event.ChannelTypingStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelTypingProducer {
    private static final String CHANNEL_TYPING_TOPIC = "channel.typing";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTypingStatus(ChannelTypingStatusChangeEvent event) {
        kafkaTemplate.send(CHANNEL_TYPING_TOPIC, event.channelId().toString(), event);
    }
}
