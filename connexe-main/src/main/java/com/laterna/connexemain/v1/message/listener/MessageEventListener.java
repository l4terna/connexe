package com.laterna.connexemain.v1.message.listener;

import com.laterna.connexemain.v1.message.event.MessageCreatedEvent;
import com.laterna.connexemain.v1.message.event.MessageDeletedEvent;
import com.laterna.connexemain.v1.message.event.MessageUpdatedEvent;
import com.laterna.connexemain.v1.message.messaging.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MessageEventListener {
    private final MessageProducer messageProducer;

    @TransactionalEventListener
    public void handleMessageCreated(MessageCreatedEvent event) {
        messageProducer.sendCreated(event);
    }

    @TransactionalEventListener
    public void handleMessageUpdated(MessageUpdatedEvent event) {
        messageProducer.sendUpdated(event);
    }

    @TransactionalEventListener
    public void handleMessageDeleted(MessageDeletedEvent event) {
        messageProducer.sendDeleted(event);
    }
}
