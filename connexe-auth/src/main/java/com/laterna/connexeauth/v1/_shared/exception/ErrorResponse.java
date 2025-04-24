package com.laterna.connexeauth.v1._shared.exception;

import lombok.Builder;
import com.laterna.connexeauth.v1._shared.exception.enumeration.ErrorType;

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