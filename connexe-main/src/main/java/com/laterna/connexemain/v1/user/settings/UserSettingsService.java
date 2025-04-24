package com.laterna.connexemain.v1.user.settings;

import com.laterna.connexemain.v1.user.User;
import com.laterna.connexemain.v1.user.UserService;
import com.laterna.connexemain.v1.user.dto.UserDTO;
import com.laterna.connexemain.v1.user.settings.dto.UserSettingsDTO;
import com.laterna.connexemain.v1.user.settings.dto.UserSettingsUpdateDTO;
import com.laterna.connexemain.v1.user.settings.enumeration.Theme;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserSettingsMapper userSettingsMapper;
    private final UserService userService;

    @Transactional
    public void setUserDefaultSettings(User user) {
        userSettingsRepository.findByUserId(user.getId()).ifPresent(userSettingsRepository::delete);

        UserSettings newSettings = UserSettings.builder()
                .userId(user.getId())
                .theme(Theme.SYSTEM)
                .build();

        userSettingsRepository.save(newSettings);
    }

    @Transactional
    public UserSettingsDTO updateUserSettings(UserSettingsUpdateDTO updateDTO, Long userId) {
        UserDTO user = userService.findById(userId);
        UserSettings settings = findUserSettingsByUserId(user.id());

        if (updateDTO.theme() != null) {
            Theme theme = Theme.fromValue(updateDTO.theme());
            settings.setTheme(theme);
        }

        return userSettingsMapper.toDTO(userSettingsRepository.save(settings));
    }

    @Transactional(readOnly = true)
    public UserSettings findUserSettingsByUserId(Long userId) {
        return userSettingsRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("User settings not found"));
    }

    @Transactional
    public UserSettingsDTO findByUserId(Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .map(userSettingsMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("User settings not found"));
    }
}
