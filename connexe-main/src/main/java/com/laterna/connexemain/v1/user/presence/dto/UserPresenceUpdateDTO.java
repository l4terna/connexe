package com.laterna.connexemain.v1.user.presence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserPresenceUpdateDTO(
        @JsonProperty("userId")
        Long userId
) {
}
