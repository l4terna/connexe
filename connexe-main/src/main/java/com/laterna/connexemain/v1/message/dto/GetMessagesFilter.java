package com.laterna.connexemain.v1.message.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@Setter
@Getter
@ParameterObject
public class GetMessagesFilter {
    @Parameter(description = "Top message id", example = "20")
    private Long before;

    @Parameter(description = "After message id", example = "10")
    private Long after;

    @Parameter(description = "Around message id", example = "10")
    private Long around;

    @Parameter(description = "Message count", example = "50")
    @Positive
    private Integer size = 50;
}
