package com.laterna.connexemain.v1.channel.typing.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChannelTypingStatus {
    STARTED(0),
    STOPPED(1);

    private final int value;

    ChannelTypingStatus(int i) {
        this.value = i;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static ChannelTypingStatus fromValue(String value) {
        for (ChannelTypingStatus status : ChannelTypingStatus.values()) {
            if (status.getValue() == Integer.parseInt(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown TypingStatus value: " + value);
    }
}
