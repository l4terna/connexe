package com.laterna.connexeauth.v1.user;

import com.laterna.connexeauth.v1.user.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
}