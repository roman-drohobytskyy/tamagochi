package com.communityz.event.realtime.tamagochi.service;

import com.communityz.event.realtime.tamagochi.dto.Hamster;
import com.communityz.event.realtime.tamagochi.events.Bellyful;
import com.communityz.event.realtime.tamagochi.events.Health;
import com.communityz.event.realtime.tamagochi.events.Morale;
import java.security.SecureRandom;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class HamsterGeneratorImpl implements HamsterGenerator {

    @Override
    public Hamster generateValidHamster() {
        return Hamster.builder()
            .name(RandomStringUtils.randomAlphabetic(7))
            .bellyful(getRandomEnumValue(Bellyful.class))
            .health(getRandomEnumValue(Health.class))
            .morale(getRandomEnumValue(Morale.class))
            .build();
    }

    @Override
    public Hamster generateInvalidHamster() {
        return Hamster.builder()
            .name("invalid")
            .build();
    }

    private <T extends Enum<?>> T getRandomEnumValue(Class<T> clazz) {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[number];
    }

}
