package com.laterna.connexemain.v1.message;

import com.laterna.connexemain.v1.message.dto.CreateMessageDTO;
import com.laterna.connexemain.v1.message.dto.GetMessagesFilter;
import com.laterna.connexemain.v1.message.dto.MessageDTO;
import com.laterna.connexemain.v1.message.dto.UpdateMessageDTO;
import com.laterna.connexemain.v1.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/channels/{channelId}/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageDTO>> getMessages(
            @PathVariable Long channelId,
            @AuthenticationPrincipal User user,
            @Valid @ModelAttribute GetMessagesFilter filter) {
        return ResponseEntity.ok(messageService.getChannelMessages(channelId, user, filter));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDTO> createMessage(
            @PathVariable Long channelId,
            @Valid @ModelAttribute CreateMessageDTO createMessageDTO,
            @AuthenticationPrincipal User user
    ) {
        MessageDTO message = messageService.create(channelId, createMessageDTO, user);

        return ResponseEntity.ok(message);
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<MessageDTO> updateMessage(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @Valid @ModelAttribute UpdateMessageDTO updateMessageDTO,
            @AuthenticationPrincipal User user
    ) {
        MessageDTO message = messageService.update(channelId, messageId, updateMessageDTO, user);

        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @AuthenticationPrincipal User user
    ) {
        messageService.delete(channelId, messageId, user);

        return ResponseEntity.noContent().build();
    }
}