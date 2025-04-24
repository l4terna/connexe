package com.laterna.connexemain.v1.user.settings.p2p;

import com.laterna.connexemain.v1.user.User;
import com.laterna.connexemain.v1.user.UserService;
import com.laterna.connexemain.v1.user.settings.p2p.dto.P2PSettingsDTO;
import com.laterna.connexemain.v1.user.settings.p2p.dto.P2PSettingsUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class P2PSettingsService {
    private final UserService userService;
    private final P2PSettingsRepository p2PSettingsRepository;
    private final P2PSettingsMapper p2PSettingsMapper;

    @Transactional
    public P2PSettingsDTO getP2PSettings(Long targetUserId, User user) {
        User targetUser = userService.findUserById(targetUserId);

        P2PSettings settings = p2PSettingsRepository.findByTargetUserIdAndSourceUserId(targetUser.getId(), user.getId())
                .orElse(createDefaultSettings(targetUserId, user.getId()));

        return p2PSettingsMapper.toDTO(settings);
    }

    @Transactional
    public P2PSettingsDTO updateSettings(Long targetUserId, P2PSettingsUpdateDTO updateDTO, User user) {
        User targetUser = userService.findUserById(targetUserId);

        P2PSettings settings = p2PSettingsRepository.findByTargetUserIdAndSourceUserId(targetUser.getId(), user.getId())
                .orElse(createDefaultSettings(targetUserId, user.getId()));

        if (updateDTO.isMuted() != null) {
            settings.setIsMuted(updateDTO.isMuted());
        }

        if (updateDTO.volumeLevel() != null) {
            settings.setVolumeLevel(updateDTO.volumeLevel());
        }

        return p2PSettingsMapper.toDTO(p2PSettingsRepository.save(settings));
    }

    public P2PSettings createDefaultSettings(Long targetUserId, Long userId) {
        return P2PSettings.builder()
                .targetUserId(targetUserId)
                .sourceUserId(userId)
                .isMuted(false)
                .volumeLevel(100)
                .build();
    }

    public P2PSettingsDTO createDefaultSettingsDTO(Long targetUserId, Long userId) {
        return p2PSettingsMapper.toDTO(createDefaultSettings(targetUserId, userId));
    }

    @Transactional(readOnly = true)
    public List<P2PSettingsDTO> findAllBySourceUserIdAndTargetUserIds(Long sourceUserId, Iterable<Long> targetUserIds) {
        return p2PSettingsRepository.findAllBySourceUserIdAndTargetUserIdIn(sourceUserId, targetUserIds)
                .stream().map(p2PSettingsMapper::toDTO).toList();
    }
}
