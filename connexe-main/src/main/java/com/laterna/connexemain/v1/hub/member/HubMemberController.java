package com.laterna.connexemain.v1.hub.member;

import com.laterna.connexemain.v1.hub.member.dto.HubMemberDTO;
import com.laterna.connexemain.v1.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hubs/{hubId}/members")
@RequiredArgsConstructor
public class HubMemberController {
    private final HubMemberService hubMemberService;

    @PostMapping
    public ResponseEntity<HubMemberDTO> create(
            @PathVariable Long hubId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(hubMemberService.create(hubId, user));
    }

    @GetMapping
    public ResponseEntity<List<HubMemberDTO>> getMembers(
            @PathVariable Long hubId,
            @RequestParam(name = "after") Long after
    ) {
        return ResponseEntity.ok(hubMemberService.getAllMembers(hubId, after));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long hubId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal User user
    ) {
        hubMemberService.deleteMember(hubId, memberId, user);
        return ResponseEntity.noContent().build();
    }
}
