package com.laterna.connexemain.v1.user.settings.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserSettingsUpdateDTO (
        @Schema(description = "Themes: 0 - DARK, 1 - LIGHT, 2 - SYSTEM",
                allowableValues = {"0", "1", "2"},
                example = "0")
        Integer theme
) {
}
