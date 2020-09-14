package com.communityz.event.realtime.tamagochi.events;

import java.util.stream.Stream;

public enum Health {

    HEALTHY(1), SICK(2), GRAVELY_SICK(3), DEAD(4), NOT_SPECIFIED(5);

    private final int orderNumber;

    Health(int i) {
        this.orderNumber = i;
    }

    public static Health valueOf(int value) {
        return Stream.of(values())
                .filter(health -> health.orderNumber == value)
                .findFirst()
                .orElse(NOT_SPECIFIED);
    }
}
