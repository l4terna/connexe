package com.laterna.connexemain.v1.user.presence.dto;

import java.util.Set;

public record TrackedUsersDTO(
        Set<Long> userIds
) {
}
