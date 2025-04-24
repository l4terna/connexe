package com.laterna.connexemain.v1.user.settings.p2p;

import com.laterna.connexemain.v1.user.User;
import com.laterna.connexemain.v1.user.settings.p2p.dto.P2PSettingsDTO;
import com.laterna.connexemain.v1.user.settings.p2p.dto.P2PSettingsUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{targetUserId}/p2p-settings")
@RequiredArgsConstructor
public class P2PSettingsController {

    private final P2PSettingsService p2PSettingsService;

    @GetMapping
    public ResponseEntity<P2PSettingsDTO> getP2PSettings(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(p2PSettingsService.getP2PSettings(targetUserId, user));
    }

    @PutMapping
    public ResponseEntity<P2PSettingsDTO> setP2PSettings(
            @PathVariable Long targetUserId,
            @RequestBody P2PSettingsUpdateDTO updateDTO,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(p2PSettingsService.updateSettings(targetUserId, updateDTO, user));
    }
}
