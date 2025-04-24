package com.laterna.connexemain.v1.message.messaging;

import com.laterna.connexemain.v1.message.event.MessageCreatedEvent;
import com.laterna.connexemain.v1.message.event.MessageDeletedEvent;
import com.laterna.connexemain.v1.message.event.MessageUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class MessageProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String MESSAGE_CREATED_TOPIC = "channel.messages.created";
    private static final String MESSAGE_UPDATED_TOPIC = "channel.messages.updated";
    private static final String MESSAGE_DELETED_TOPIC = "channel.messages.deleted";

    public void sendCreated(MessageCreatedEvent event) {
        kafkaTemplate.send(MESSAGE_CREATED_TOPIC, event.channelId().toString(), event);
    }

    public void sendUpdated(MessageUpdatedEvent event) {
        kafkaTemplate.send(MESSAGE_UPDATED_TOPIC, event.channelId().toString(), event);
    }

    public void sendDeleted(MessageDeletedEvent event) {
        kafkaTemplate.send(MESSAGE_DELETED_TOPIC, event.channelId().toString(), event);
    }
}
