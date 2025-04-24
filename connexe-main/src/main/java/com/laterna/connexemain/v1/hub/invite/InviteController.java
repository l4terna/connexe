package com.laterna.connexemain.v1.hub.invite;

import com.laterna.connexemain.v1.hub.invite.dto.AcceptInviteDTO;
import com.laterna.connexemain.v1.hub.invite.dto.CreateInviteDTO;
import com.laterna.connexemain.v1.hub.invite.dto.InviteDTO;
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
@RequestMapping("/api/v1/hubs/{hubId}/invites")
@RequiredArgsConstructor
public class InviteController {
    private final InviteService inviteService;

    @GetMapping
    public ResponseEntity<Page<InviteDTO>> getAllInvites(
            @PathVariable Long hubId,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(inviteService.getAllInvites(hubId, pageable));
    }

    @PostMapping
    public ResponseEntity<InviteDTO> createInvite(
            @PathVariable Long hubId,
            @Valid @RequestBody CreateInviteDTO createInviteDTO,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(inviteService.create(hubId, createInviteDTO, user));
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptInvite(
            @PathVariable Long hubId,
            @RequestBody AcceptInviteDTO acceptInviteDTO,
            @AuthenticationPrincipal User user
    ) {
        inviteService.accept(hubId, acceptInviteDTO, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{inviteId}")
    public ResponseEntity<Void> deleteInvite(
            @PathVariable Long hubId,
            @PathVariable Long inviteId,
            @AuthenticationPrincipal User user
    ) {
        inviteService.delete(hubId, inviteId, user);
        return ResponseEntity.noContent().build();
    }
}
