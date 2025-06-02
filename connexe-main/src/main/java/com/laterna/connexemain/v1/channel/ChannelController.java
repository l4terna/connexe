package com.laterna.connexemain.v1.channel;

import com.laterna.connexemain.v1.channel.dto.DirectChannelDTO;
import com.laterna.connexemain.v1.channel.dto.HubChannelDTO;
import com.laterna.connexemain.v1.channel.dto.CreateDirectChannelDTO;
import com.laterna.connexemain.v1.channel.dto.UpdateChannelDTO;
import com.laterna.connexemain.v1.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/channels")
public class ChannelController {

    private final ChannelService channelService;
    private final ChannelCreationService channelCreationService;

    @GetMapping
    public ResponseEntity<Page<DirectChannelDTO>> getDirectAndGroupChannels(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(channelService.getDirectAndGroupChannels(pageable, user));
    }

    @PostMapping
    public ResponseEntity<DirectChannelDTO> createDirectChannel(
            @Valid @RequestBody CreateDirectChannelDTO createChannelDTO,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(channelCreationService.createDirectChannel(createChannelDTO, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HubChannelDTO> updateChannel(
            @PathVariable Long id,
            @Valid @RequestBody UpdateChannelDTO updateChannelDTO,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(channelService.update(id, updateChannelDTO, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannel(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        channelService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
