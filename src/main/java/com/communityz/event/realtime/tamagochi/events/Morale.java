package com.communityz.event.realtime.tamagochi.events;

import java.util.stream.Stream;

public enum Morale {

    CHEERFUL(1), PLAYFUL(2), NEUTRAL(3), DEPRESSED(4), NOT_SPECIFIED(5);

    private final int orderNumber;

    Morale(int i) {
        this.orderNumber = i;
    }

    public static Morale valueOf(int value) {
        return Stream.of(values())
                .filter(morale -> morale.orderNumber == value)
                .findFirst()
                .orElse(NOT_SPECIFIED);
    }
}
