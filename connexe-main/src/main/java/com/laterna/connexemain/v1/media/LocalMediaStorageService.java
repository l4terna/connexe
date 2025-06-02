package com.laterna.connexemain.v1.media;

import com.laterna.connexemain.v1._shared.integration.user.session.UserSessionServiceClient;
import com.laterna.connexemain.v1.media.dto.MediaRetrieveDTO;
import com.laterna.connexemain.v1.media.enumeration.MediaVisibility;
import com.laterna.connexemain.v1.media.service.MediaStorageService;
import com.laterna.connexemain.v1.media.sign.MediaSignService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalMediaStorageService implements MediaStorageService {
    private final UserSessionServiceClient userSessionServiceClient;
    private final MediaSignService mediaSignService;
    @Value("${app.media.dir}")
    private String mediaDir;

    private final MediaRepository mediaRepository;

    @Override
    @Transactional
    public Media store(MultipartFile file,
                       MediaVisibility mediaVisibility,
                       Long createdById) {
        createDirsIfNotExists();

        String filename = UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        String filepath = mediaDir + "/tmp/" + filename;

        try {
            file.transferTo(new File(filepath));
        } catch (IOException e) {
            log.error("Error during file saving {}", e.getMessage());
            throw new RuntimeException(e);
        }

        Media media = Media.builder()
                .storageKey(FilenameUtils.getBaseName(filename))
                .extension(FilenameUtils.getExtension(filename))
                .visibility(mediaVisibility)
                .size(file.getSize())
                .contentType(file.getContentType())
                .createdBy(createdById)
                .build();

        Media savedMedia = mediaRepository.save(media);

        try {
            Files.move((new File(filepath)).toPath(), (new File(mediaDir + "/" + filename)).toPath());
        } catch (IOException e) {
            log.error("Error during file saving {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return savedMedia;
    }

    @Override
    @Transactional(readOnly = true)
    public MediaRetrieveDTO retrieve(String storageKey, String sign, Long userId, String fingerprint) {
        Media media = mediaRepository.findFirstByStorageKey(storageKey)
                .orElseThrow(() -> new EntityNotFoundException("Media not found"));

        if (media.getVisibility() == MediaVisibility.PRIVATE) {
            if ((sign == null || userId == null || fingerprint == null)
                    || (userSessionServiceClient.getUserIdByFingerprint(fingerprint, userId) == null
                        || !mediaSignService.validateSign(sign, userId))
            ) {
                throw new AccessDeniedException("Access denied");
            }
        }

        String filename = media.getStorageKey() + "." + media.getExtension();
        String filepath = mediaDir + "/" + filename;

        File file = new File(filepath);
        if (!file.exists()) {
            log.error("File not found on disk: {}", filepath);
            throw new RuntimeException("File not found: " + filepath);
        }

        try {
            InputStream inputStream = new FileInputStream(file);
            return new MediaRetrieveDTO(inputStream, media.getContentType());
        } catch (FileNotFoundException e) {
            log.error("Error opening file stream: {}", filepath, e);
            throw new RuntimeException("Error opening file stream: " + filepath, e);
        }
    }

    private void createDirsIfNotExists() {
        File mDir = new File(mediaDir);
        File tmpDir = new File(mediaDir + "/tmp");

        if (!mDir.exists()) {
            if (!mDir.mkdirs()) {
                log.error("Error during creating media directory {}", mDir.getAbsolutePath());
                throw new RuntimeException("Unable to create directory " + mediaDir);
            }
        }

        if (!tmpDir.exists()) {
            if (!tmpDir.mkdirs()) {
                log.error("Error during creating media temporary directory {}", tmpDir.getAbsolutePath());
                throw new RuntimeException("Unable to create directory " + tmpDir);
            }
        }
    }

    @Transactional
    @Override
    public void delete(String storageKey) {
        Media media = mediaRepository.findFirstByStorageKey(storageKey)
                .orElseThrow(() -> new EntityNotFoundException("Media not found"));

        mediaRepository.delete(media);

        String filepath = mediaDir + "/" + media.getStorageKey() + "." + media.getExtension();
        File file = new File(filepath);

        if (!file.delete()) {
            log.error("Error deleting file {}", filepath);
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanMediaTempDir() {
        String tmpDir = mediaDir + "/tmp";
        File dir = new File(tmpDir);

        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (file.lastModified() < System.currentTimeMillis() - 3600000) {
                    file.delete();
                }
            }
        }
    }
}
