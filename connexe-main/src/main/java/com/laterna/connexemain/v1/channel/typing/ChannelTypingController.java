package com.laterna.connexemain.v1.channel.typing;

import com.laterna.connexemain.v1.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/channels")
public class ChannelTypingController {
    private final ChannelTypingService channelTypingService;

    @GetMapping("/{channelId}/typing-users")
    public ResponseEntity<List<UserDTO>> getTypingUsers(
            @PathVariable Long channelId
    ) {
        return ResponseEntity.ok(channelTypingService.getChannelTypingUsers(channelId));
    }
}
