package com.laterna.connexemain.v1.hub.dto;

import org.springframework.web.multipart.MultipartFile;

public record UpdateHubDTO (
        String name,
        String type,
        MultipartFile avatar
){
}
