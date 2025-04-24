package com.laterna.connexemain.v1.user.listener;

import com.laterna.connexemain.v1.user.UserService;
import com.laterna.connexemain.v1.user.dto.UserCreateDTO;
import com.laterna.proto.v1.UserDataProto;
import com.laterna.proto.v1.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMessagingEventListener {

    private final UserService userService;

    @EventListener
    public void handleUserEvent(UserEvent userEvent) {
        switch (userEvent.getEventType()) {
            case USER_CREATED -> {
                UserDataProto userData = userEvent.getCreatedUser();

                UserCreateDTO createDTO = UserCreateDTO.builder()
                        .userId(userData.getUserId())
                        .email(userData.getEmail())
                        .login(userData.getLogin())
                        .build();

                userService.createUser(createDTO);
            }
        }
    }
}
