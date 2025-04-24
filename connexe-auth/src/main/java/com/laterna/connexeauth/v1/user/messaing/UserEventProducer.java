package com.laterna.connexeauth.v1.user.messaing;

import com.laterna.proto.v1.UserEventProduce;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventProducer {
    private final StreamBridge streamBridge;
    private static final String OUTPUT_BINDING = "userEvent-out-0";

    @TransactionalEventListener
    public boolean handleUserProduceEvent(UserEventProduce userEventProduce) {
        return streamBridge.send(
                OUTPUT_BINDING,
                MessageBuilder.withPayload(userEventProduce.getUserEvent()).build()
        );
    }
}
