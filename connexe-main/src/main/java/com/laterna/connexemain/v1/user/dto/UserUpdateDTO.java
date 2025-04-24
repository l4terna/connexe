package com.laterna.connexemain.v1.user.dto;


import org.springframework.web.multipart.MultipartFile;

public record UserUpdateDTO(
        String login,
        MultipartFile avatar
) {
}
