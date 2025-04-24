package com.laterna.connexemain.v1.user.settings;

import com.laterna.connexemain.v1.user.settings.dto.UserSettingsDTO;
import com.laterna.connexemain.v1.user.settings.dto.UserSettingsUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/settings")
@RequiredArgsConstructor
public class UserSettingsController {
    private final UserSettingsService userSettingsService;

    @GetMapping
    public ResponseEntity<UserSettingsDTO> getUserSettings(@PathVariable Long userId) {
        return ResponseEntity.ok(userSettingsService.findByUserId(userId));
    }

    @PutMapping
    public ResponseEntity<UserSettingsDTO> updateUserSettings(
            @RequestBody UserSettingsUpdateDTO updateDTO,
            @PathVariable Long userId) {
        return ResponseEntity.ok(userSettingsService.updateUserSettings(updateDTO, userId));
    }
}
