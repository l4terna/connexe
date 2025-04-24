package com.laterna.connexemain.v1.channel.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;

@ToString
public enum ChannelType {
    VOICE(0),
    TEXT(1),
    DC(2),
    GROUP_DC(3);

    private final int value;

    ChannelType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static ChannelType fromValue(String value) {
        for (ChannelType type : values()) {
            if (type.value == Integer.parseInt(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ChannelType value: " + value);
    }
}
