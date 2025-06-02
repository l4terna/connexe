package com.laterna.connexemain.v1.media.sign;

import com.laterna.connexemain.v1.media.Media;
import com.laterna.connexemain.v1.media.MediaService;
import com.laterna.connexemain.v1.media.sign.dto.MediaSignDTO;
import com.laterna.connexemain.v1.media.sign.dto.CreateMediaSignDTO;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaSignService {
    private static final String ALGORITHM = "HmacSHA256";
    private static final String secretKey = "secret";

    private static final long HOURS_OFFSET = 1L;
    private final MediaService mediaService;
    private final MediaSignRepository mediaSignRepository;

    @Transactional
    public Map<String, MediaSignDTO> signMedia(CreateMediaSignDTO signMediaDTO, User user) {
        Map<String, MediaSignDTO> keysWithHashes = new HashMap<>();

        List<Media> medias = mediaService.findAllByStorageKeys(
                signMediaDTO.storageKeys().stream().distinct().toList()
        );

        List<MediaSign> signsToSave = medias.stream()
                .distinct()
                .map(media -> {
                    String sign = generateSignKey(media.getStorageKey() + UUID.randomUUID() + Instant.now().toEpochMilli());

                    MediaSignDTO signDTO = MediaSignDTO.builder()
                            .sign(sign)
                            .expiresAt(LocalDateTime.now().plusHours(HOURS_OFFSET).toInstant(ZonedDateTime.now().getOffset()))
                            .userId(user.getId())
                            .build();

                    keysWithHashes.put(media.getStorageKey(), signDTO);

                    return MediaSign.builder()
                            .media(media)
                            .expiresAt(signDTO.expiresAt())
                            .userId(user.getId())
                            .sign(sign)
                            .build();
                })
                .collect(Collectors.toList());

        mediaSignRepository.saveAll(signsToSave);

        return keysWithHashes;
    }

    public String generateSignKey(String value) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(secretKeySpec);

            byte[] hashedValue = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));

            // getUrlEncode - для url- безопасности
            return Base64.getUrlEncoder().encodeToString(hashedValue);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException e) {
            log.error("Error while generating signing key: {}", e);
            throw new RuntimeException("Error during generating sign key", e);
        }
    }

    @Transactional(readOnly = true)
    public boolean validateSign(String sign, Long userId) {
        MediaSign mediaSign = mediaSignRepository.findBySignAndUserId(sign, userId).orElse(null);

        if (mediaSign == null) return false;

        return mediaSign.getExpiresAt().isAfter(Instant.now());
    }
}
