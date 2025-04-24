package com.laterna.connexemain.v1.channel.typing.listener;

import com.laterna.connexemain.v1.channel.typing.event.ChannelTypingStatusChangeEvent;
import com.laterna.connexemain.v1.channel.typing.messaging.ChannelTypingProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelTypingEventListener {
    private final ChannelTypingProducer channelTypingProducer;

    @EventListener
    public void handleTypingStatusChange(ChannelTypingStatusChangeEvent event) {
        channelTypingProducer.sendTypingStatus(event);
    }
}
