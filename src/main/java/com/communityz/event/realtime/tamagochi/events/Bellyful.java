package com.communityz.event.realtime.tamagochi.events;

import java.util.stream.Stream;

public enum  Bellyful {

    SATISFIED(1), HUNGRY(2), STARVING(3), NOT_SPECIFIED(4);

    private final int orderNumber;

    Bellyful(int i) {
        this.orderNumber = i;
    }

    public static Bellyful valueOf(int value) {
        return Stream.of(values())
                .filter(bellyful -> bellyful.orderNumber == value)
                .findFirst()
                .orElse(NOT_SPECIFIED);
    }
}
