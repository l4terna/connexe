package com.laterna.connexemain.v1.user;

import com.laterna.connexemain.v1.user.dto.GetUserFilter;
import com.laterna.connexemain.v1.user.dto.UserDTO;
import com.laterna.connexemain.v1.user.dto.UserProfileDTO;
import com.laterna.connexemain.v1.user.dto.UserUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/@me")
    public ResponseEntity<UserDTO> updateMe(
            @Valid @ModelAttribute UserUpdateDTO updateDTO,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(userService.updateMe(updateDTO, user));
    }

    @DeleteMapping("/@me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
        userService.deleteMe(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/@me")
    public ResponseEntity<UserDTO> getMe(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(userService.getMe(user));
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileDTO> getProfile(
            @PathVariable Long id,
            @ModelAttribute GetUserFilter filter
            ) {
        return ResponseEntity.ok(userService.getProfile(id, filter));
    }

}
