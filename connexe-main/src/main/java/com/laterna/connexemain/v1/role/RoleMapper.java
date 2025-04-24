package com.laterna.connexemain.v1.role;

import com.laterna.connexemain.v1.role.dto.RoleDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
}
