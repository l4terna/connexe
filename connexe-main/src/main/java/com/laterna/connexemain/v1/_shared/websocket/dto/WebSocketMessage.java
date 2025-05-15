package com.laterna.connexemain.v1._shared.websocket.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.laterna.connexemain.v1._shared.websocket.enumeration.WebSocketMessageType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WebSocketMessage {
    private Map<String, Object> payload = new HashMap<>();

    private WebSocketMessage(WebSocketMessageType type) {
        payload.put("type", type);
    }

    public static WebSocketMessageBuilder builder(WebSocketMessageType type) {
        return new WebSocketMessageBuilder(type);
    }

    @JsonAnyGetter
    public Map<String, Object> getPayload() {
        return payload;
    }

    @JsonAnySetter
    public void setPayload(String name, Object value) {
        payload.put(name, value);
    }

    public static class WebSocketMessageBuilder {
        private final WebSocketMessage message;

        public WebSocketMessageBuilder(WebSocketMessageType type) {
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