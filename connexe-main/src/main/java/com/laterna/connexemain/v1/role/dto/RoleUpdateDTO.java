package com.laterna.connexemain.v1.role.dto;

public record RoleUpdateDTO(
        String name,
        String permissions,
        String color
) {
}
