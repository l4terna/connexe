package com.laterna.connexemain.v1._shared.model.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Setter
@Getter
@ParameterObject
public class PageableDTO {
    @Parameter(description = "Page number", example = "0")
    @PositiveOrZero
    protected Integer page = 0;

    @Parameter(description = "Page size", example = "10")
    @Positive
    protected Integer size = 10;

    @Parameter(description = "Sort field", example = "id")
    protected String sortBy = "id";

    @Parameter(description = "Sort direction", example = "ASC")
    protected String sortDirection = "ASC";

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortBy);
    }
}