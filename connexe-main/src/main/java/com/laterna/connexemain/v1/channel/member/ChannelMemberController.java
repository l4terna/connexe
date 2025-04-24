package com.laterna.connexemain.v1.channel.member;

import com.laterna.connexemain.v1.channel.member.dto.ChannelMemberDTO;
import com.laterna.connexemain.v1.channel.member.dto.CreateChannelMemberDTO;
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
@RequestMapping("/api/v1/channels/{channelId}/members")
@RequiredArgsConstructor
public class ChannelMemberController {

    private final ChannelMemberService channelMemberService;
    private final ChannelMemberCreationService channelMemberCreationService;

    @GetMapping
    public ResponseEntity<Page<ChannelMemberDTO>> getChannelMembers(
            @PathVariable Long channelId,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(channelMemberService.getChannelMembers(channelId, pageable));
    }

    @PostMapping
    public ResponseEntity<ChannelMemberDTO> createChannelMember(
        @Valid @RequestBody CreateChannelMemberDTO createMemberDTO,
        @PathVariable Long channelId
    ) {
        channelMemberCreationService.create(channelId, createMemberDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteChannelMember(
            @PathVariable Long channelId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal User user) {
        channelMemberService.delete(channelId, memberId, user);
        return ResponseEntity.noContent().build();
    }
}
