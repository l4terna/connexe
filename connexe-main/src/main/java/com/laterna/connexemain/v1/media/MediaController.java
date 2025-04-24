package com.laterna.connexemain.v1.media;

import com.laterna.connexemain.v1.media.dto.MediaRetrieveDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/media")
public class MediaController {

    private final LocalMediaStorageService localMediaStorageService;

    @GetMapping("/{storageKey}")
    public ResponseEntity<InputStreamResource> retrieveMedia(@PathVariable String storageKey) {
        MediaRetrieveDTO dto = localMediaStorageService.retrieve(storageKey);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dto.contentType()))
                .body(new InputStreamResource(dto.inputStream()));
    }
}
