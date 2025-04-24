package com.laterna.connexemain.v1._shared.websocket.dto;

import lombok.Builder;
import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;
import java.net.URI;
import java.security.Principal;
import java.util.Map;

@Builder
public record WebSocketSessionDTO (
    String id,
    URI uri,
    Principal principal,
    Map<String, Object> attributes

) implements Serializable {
    public static WebSocketSessionDTO from(WebSocketSession session) {
        return WebSocketSessionDTO.builder()
                .id(session.getId())
                .uri(session.getUri())
                .principal(session.getPrincipal())
                .attributes(session.getAttributes())
                .build();
    }
}