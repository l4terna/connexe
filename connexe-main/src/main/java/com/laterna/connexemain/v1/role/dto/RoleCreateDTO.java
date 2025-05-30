package com.laterna.connexemain.v1.role.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleCreateDTO(
        @NotBlank
        String name,
        @NotBlank
        String color,
        @NotBlank
        String permissions
) {
}
