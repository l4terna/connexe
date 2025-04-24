package com.laterna.connexemain.v1.channel.voice;

import com.laterna.connexemain.v1.channel.voice.dto.ChannelVoiceDTO;
import com.laterna.connexemain.v1.channel.voice.dto.ChannelVoiceJoinDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelVoiceWebSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessageNewToActiveChannel(ChannelVoiceJoinDTO joinVoiceDTO) {
        simpMessagingTemplate.convertAndSend(
                "/v1/topic/channels/" + joinVoiceDTO.channelId() + "/voice/new",
                joinVoiceDTO
        );
    }

    public void sendMessageLeftToActiveChannel(ChannelVoiceDTO voiceDTO) {
        simpMessagingTemplate.convertAndSend(
                "/v1/topic/channels/" + voiceDTO.channelId() + "/voice/left",
                voiceDTO
        );
    }
}
