package com.laterna.connexemain.v1.channel.voice.listener;

import com.laterna.connexemain.v1.channel.voice.ChannelVoiceService;
import com.laterna.connexemain.v1.channel.voice.event.ChannelVoiceDisconnectEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelVoiceListener {

    private final ChannelVoiceService channelVoiceService;

    @EventListener
    public void handleDisconnectEvent(ChannelVoiceDisconnectEvent event) {
        channelVoiceService.removeUserActiveChannel(event.userId());
    }
}
