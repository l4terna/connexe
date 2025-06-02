package com.laterna.connexemain.v1.media.sign.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateMediaSignDTO(
        @NotEmpty
        List<String> storageKeys
) {

}
