package com.laterna.connexemain.v1.user.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Setter
@Getter
public class GetUserFilter {
    @Parameter(description = "Hub id", example = "1")
    @Positive
    private Long hubId;
}
