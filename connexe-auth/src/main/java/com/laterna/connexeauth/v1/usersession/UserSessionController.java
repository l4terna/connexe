package com.laterna.connexeauth.v1.usersession;

import com.laterna.connexeauth.v1.user.User;
import com.laterna.connexeauth.v1.usersession.dto.UserSessionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-sessions")
@RequiredArgsConstructor
public class UserSessionController {
    private final UserSessionService userSessionService;

    @GetMapping
    public ResponseEntity<Page<UserSessionDTO>> getUserSessions(
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal User user,
            @CookieValue("__fprid") String fingerprint
    ) {
        return ResponseEntity.ok(userSessionService.getUserSessions(pageable, user, fingerprint));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revokeUserSession(@PathVariable Long id, @AuthenticationPrincipal User user) {
        userSessionService.deactivateSession(id, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> revokeAllOtherUserSessions(
            @AuthenticationPrincipal User user,
            @CookieValue("__fprid") String fingerprint
    ) {
        userSessionService.deactivateAllOtherUserSessions(user, fingerprint);
        return ResponseEntity.noContent().build();
    }
}
