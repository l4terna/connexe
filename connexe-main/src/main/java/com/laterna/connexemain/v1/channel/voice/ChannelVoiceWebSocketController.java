package com.laterna.connexemain.v1.channel.voice;

import com.laterna.connexemain.v1.channel.voice.dto.ChannelVoiceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChannelVoiceWebSocketController {

    private final ChannelVoiceService channelVoiceService;

    @MessageMapping("/voice/join")
    public void joinToVoice(@Payload ChannelVoiceDTO joinDTO) {
        channelVoiceService.addUserToActiveChannel(joinDTO);
    }
}
