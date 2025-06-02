package com.laterna.connexemain.v1.media.sign;

import com.laterna.connexemain.v1.media.sign.dto.MediaSignDTO;
import com.laterna.connexemain.v1.media.sign.dto.CreateMediaSignDTO;
import com.laterna.connexemain.v1.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/media-sign")
@RequiredArgsConstructor
public class MediaSignController {
    private final MediaSignService mediaSignService;

    @PostMapping
    public ResponseEntity<Map<String, MediaSignDTO>> sign(
            @Valid @RequestBody CreateMediaSignDTO signMediaDTO,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(mediaSignService.signMedia(signMediaDTO, user));
    }
}
