package com.laterna.connexemain.v1._shared.websocket.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WebSocketMessage {
    private String type;

    private Map<String, Object> payload = new HashMap<>();

    private WebSocketMessage(String type) {
        this.type = type;
    }

    public static WebSocketMessageBuilder builder(String type) {
        return new WebSocketMessageBuilder(type);
    }

    public static class WebSocketMessageBuilder {
        private final WebSocketMessage message;

        public WebSocketMessageBuilder(String type) {
            this.message = new WebSocketMessage(type);
        }

        public WebSocketMessageBuilder add(String key, Object value) {
            message.getPayload().put(key, value);
            return this;
        }

        public WebSocketMessage build() {
            return message;
        }
    }
}