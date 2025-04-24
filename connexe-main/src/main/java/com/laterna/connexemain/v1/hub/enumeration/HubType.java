package com.laterna.connexemain.v1.hub.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HubType {
    PRIVATE(0),
    PUBLIC(1);

    private final int value;

    HubType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public static HubType fromValue(String value) {
        for (HubType type : values()) {
            if (type.value == Integer.parseInt(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown hub type value: " + value);
    }
}
