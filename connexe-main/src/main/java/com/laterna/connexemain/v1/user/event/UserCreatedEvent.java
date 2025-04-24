package com.laterna.connexemain.v1.user.event;

import com.laterna.connexemain.v1.user.User;

public record UserCreatedEvent(
        User user
) {
}
