package com.laterna.connexemain.v1.user.settings.p2p.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record P2PSettingsUpdateDTO(
    Boolean isMuted,
    @Max(200)
    @Min(0)
    @Schema(description = "Volume level. Max - 200. Min - 0")
    Integer volumeLevel
) {
}
