package com.laterna.connexemain.v1.channel;

import com.laterna.connexemain.v1.channel.dto.HubChannelDTO;
import com.laterna.connexemain.v1.channel.dto.CreateHubChannelDTO;
import com.laterna.connexemain.v1.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hubs/{hubId}/channels")
@RequiredArgsConstructor
public class CategoryChannelController {
    private final ChannelCreationService channelCreationService;

    @PostMapping
    public ResponseEntity<HubChannelDTO> createHubChannel(
            @PathVariable Long hubId,
            @Valid @RequestBody CreateHubChannelDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(channelCreationService.createHubChannel(hubId, dto, user));
    }
}
