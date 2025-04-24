package com.laterna.connexemain.v1.channel.typing;

import com.laterna.connexemain.v1.channel.typing.dto.UpdateTypingStatusDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChannelTypingWebSocketController {

    private final ChannelTypingService channelTypingService;

    @MessageMapping("/channels/{channelId}/typing")
    public void updateTypingIndicator(
            @Payload UpdateTypingStatusDTO updateTypingStatusDTO,
            @DestinationVariable Long channelId,
            Principal principal
    ) {
        channelTypingService.updateTypingIndicator(updateTypingStatusDTO, channelId, principal);
    }
}
