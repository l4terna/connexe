package com.laterna.connexemain.v1._shared.exception;

import com.laterna.connexemain.v1._shared.exception.enumeration.ErrorType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ErrorResponse(
    String message,
    ErrorType type,
    int statusCode,
    Instant timestamp,
    String path,
    List<String> errors
) {}