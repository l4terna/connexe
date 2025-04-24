package com.laterna.connexeauth.v1.user.listener;

import com.laterna.connexeauth.v1.user.UserService;
import com.laterna.proto.v1.UserEvent;
import com.laterna.proto.v1.UserEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;

    @EventListener
    public void handleUserEvent(UserEvent userEvent) {
        if (userEvent.getEventType() == UserEventType.USER_DELETED) {
            userService.delete(userEvent.getDeletedUserId());
        } else if (userEvent.getEventType() == UserEventType.USER_UPDATED) {
            userService.update(userEvent.getUpdatedUser().getUserId(), userEvent.getUpdatedUser().getLogin());
        }
    }
}
