package com.laterna.connexemain.v1.category;

import com.laterna.connexemain.v1.category.dto.CategoryDTO;
import com.laterna.connexemain.v1.hub.HubMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {HubMapper.class})
public interface CategoryMapper {
    CategoryDTO toDTO(Category category);
}
