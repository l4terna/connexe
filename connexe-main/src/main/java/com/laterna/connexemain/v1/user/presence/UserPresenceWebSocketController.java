package com.laterna.connexemain.v1.user.presence;

import com.laterna.connexemain.v1.user.presence.dto.UserPresenceUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserPresenceWebSocketController {
    private final UserPresenceManageService userPresenceManageService;

    @MessageMapping("/presence")
    public void updatePresence (
            UserPresenceUpdateDTO statusUpdate,
            SimpMessageHeaderAccessor headerAccessor) {
        // TODO: ДОПИСАТЬ ЛОГИКУ ДЛЯ СТАТУСОВ "AWAY", "DO NOT DISTURB" и т.д
    }
}
