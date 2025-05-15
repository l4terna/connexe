package com.laterna.connexeauth.v1.user;

import com.laterna.connexeauth.v1.usersession.UserSessionService;
import com.laterna.proto.v1.UserLastActivityUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserSessionService userSessionService;

    @GetMapping(value = "/{id}/last-activity", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLastActivity(@PathVariable Long id) {
        return ResponseEntity.ok(userSessionService.getLastActivity(id));
    }

    @PutMapping("/{id}/last-activity")
    public ResponseEntity<Void> updateLastActivity(@PathVariable Long id, @RequestBody UserLastActivityUpdateDTO userLastActivityUpdate) {
        userSessionService.updateLastActivity(id, userLastActivityUpdate);
        return ResponseEntity.ok().build();
    }
}
