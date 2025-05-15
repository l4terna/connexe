package com.laterna.connexemain.v1.hub.member.role;

import com.laterna.connexemain.v1.hub.member.role.dto.HubMemberRoleResponseDTO;
import com.laterna.connexemain.v1.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hubs/{hubId}/members/{memberId}/roles")
@RequiredArgsConstructor
public class HubMemberRoleController {
    private final HubMemberRoleService hubMemberRoleService;

    @PostMapping
    public ResponseEntity<Void> create(
            @PathVariable Long hubId,
            @PathVariable Long memberId,
            @RequestBody HubMemberRoleResponseDTO createDTO,
            @AuthenticationPrincipal User user
    ) {
        hubMemberRoleService.create(hubId, memberId, createDTO, user);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long hubId,
            @PathVariable Long memberId,
            @PathVariable Long roleId,
            @AuthenticationPrincipal User user
    ) {
        hubMemberRoleService.delete(hubId, memberId, roleId, user);

        return ResponseEntity.noContent().build();
    }
}

