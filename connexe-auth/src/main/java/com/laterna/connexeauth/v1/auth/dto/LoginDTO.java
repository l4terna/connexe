package com.laterna.connexeauth.v1.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @Email
        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotBlank
        String fingerprint
) {
}
