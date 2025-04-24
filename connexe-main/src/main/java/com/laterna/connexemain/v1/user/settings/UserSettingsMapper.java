package com.laterna.connexemain.v1.user.settings;

import com.laterna.connexemain.v1.user.settings.dto.UserSettingsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSettingsMapper {
    UserSettingsDTO toDTO(UserSettings userSettings);
}
