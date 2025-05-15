package com.laterna.connexemain.v1.message.read.dto;


import java.util.Set;

public record MessageBulkReadDTO(
        Set<Long> messageIds
) {
}
