package com.laterna.connexemain.v1.hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record CreateHubDTO(
        @NotBlank
        @Size(max = 100)
        String name,
        MultipartFile avatar,
        @NotBlank
        String type
){
}
