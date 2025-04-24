package com.laterna.connexemain.v1.user.listener;

import com.laterna.connexemain.v1.user.event.UserCreatedEvent;
import com.laterna.connexemain.v1.user.settings.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserSettingsService userSettingsService;

    @TransactionalEventListener
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        userSettingsService.setUserDefaultSettings(event.user());
    }
}
