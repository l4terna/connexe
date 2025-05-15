package com.laterna.connexemain.v1.message.read;

import com.laterna.connexemain.v1.message.read.dto.MessageBulkReadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
public class MessageReadStatusController {
    private final MessageReadStatusMarksService messageReadStatusMarksService;

    @MessageMapping("/channels/{channelId}/messages/bulk-read")
    public void bulkRead(
            @DestinationVariable Long channelId,
            @Payload MessageBulkReadDTO messageBulkReadDTO,
            Principal principal
    ) {
        messageReadStatusMarksService.bulkRead(channelId, messageBulkReadDTO, principal);
    }

    @MessageMapping("/channels/{channelId}/messages/bulk-read-all")
    public void bulkReadAll(
            @DestinationVariable Long channelId,
            Principal principal
    ) {
        messageReadStatusMarksService.bulkReadAll(channelId, principal);
    }
}
