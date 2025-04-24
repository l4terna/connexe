package com.laterna.connexemain.v1.media.dto;

import java.io.InputStream;

public record MediaRetrieveDTO (
        InputStream inputStream,
        String contentType
) { }
