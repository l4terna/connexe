package com.laterna.connexemain.v1.user.settings.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Theme {
    DARK(0),
    LIGHT(1),
    SYSTEM(2);

    private final int value;

    Theme(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public static Theme fromValue(Integer value) {
        for (Theme theme : values()) {
            if (theme.value == value) {
                return theme;
            }
        }
        throw new IllegalArgumentException("Unknown theme value: " + value);
    }
}
