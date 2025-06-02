package com.laterna.connexemain.v1.media;

import com.laterna.connexemain.v1.media.dto.MediaRetrieveDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/media")
public class MediaController {

    private final LocalMediaStorageService localMediaStorageService;

    @GetMapping("/{storageKey}")
    public ResponseEntity<InputStreamResource> retrieveMedia(
            @PathVariable String storageKey,
            @RequestParam(name = "sign", required = false) String sign,
            @RequestParam(name = "user_id", required = false) Long userId,
            @CookieValue(name = "__fprid", required = false) String fingerprint
    ) {
        MediaRetrieveDTO dto = localMediaStorageService.retrieve(storageKey, sign, userId, fingerprint);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dto.contentType()))
                .body(new InputStreamResource(dto.inputStream()));
    }
}
