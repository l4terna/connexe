package com.laterna.connexemain.v1.user.settings.dto;

import com.laterna.connexemain.v1.user.settings.enumeration.Theme;

public record UserSettingsDTO(
        Long userId,
        Theme theme
) {
}
