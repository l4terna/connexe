package com.laterna.connexemain.v1.channel.dto;

import com.laterna.connexemain.v1.category.dto.CategoryDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record HubEntitiesDTO (
        List<CategoryDTO> categories,
        List<HubChannelDTO> channels
) {
}
