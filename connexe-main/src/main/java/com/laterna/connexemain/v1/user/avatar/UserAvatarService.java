package com.laterna.connexemain.v1.user.avatar;

import com.laterna.connexemain.v1.media.LocalMediaStorageService;
import com.laterna.connexemain.v1.media.Media;
import com.laterna.connexemain.v1.media.enumeration.MediaVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserAvatarService {
    private final UserAvatarRepository userAvatarRepository;
    private final LocalMediaStorageService localMediaStorageService;

    @Transactional
    public void save(MultipartFile file, Long userId) {
        Media media = localMediaStorageService.store(file, MediaVisibility.PUBLIC, userId);

        UserAvatar userAvatar = UserAvatar.builder()
                .mediaId(media.getId())
                .userId(userId)
                .build();

        userAvatarRepository.save(userAvatar);
    }
}
