package com.laterna.connexemain.v1.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    public List<Media> findAllByStorageKeys(Iterable<String> storageKeys) {
        return mediaRepository.findByStorageKeyIn(storageKeys);
    }
}
