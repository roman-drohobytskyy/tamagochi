package com.communityz.event.realtime.tamagochi.service;

import com.communityz.event.realtime.tamagochi.dto.Hamster;
import com.communityz.event.realtime.tamagochi.events.Bellyful;
import com.communityz.event.realtime.tamagochi.events.Health;
import com.communityz.event.realtime.tamagochi.events.Morale;
import com.communityz.event.realtime.tamagochi.publisher.PubSubMessagePublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class ScheduledTask {

    public static final int TWO = 2;
    private final PubSubMessagePublisher pubSubMessagePublisher;
    private final ObjectMapper objectMapper;

    public ScheduledTask(PubSubMessagePublisher pubSubMessagePublisher, ObjectMapper objectMapper) {
        this.pubSubMessagePublisher = pubSubMessagePublisher;
        this.objectMapper = objectMapper;
    }


    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        Random random = new Random();
        Bellyful bellyful = Bellyful.valueOf(random.nextInt(Bellyful.values().length - TWO) + 1);
        Health health = Health.valueOf(random.nextInt(Health.values().length - TWO) + 1);
        Morale morale = Morale.valueOf(random.nextInt(Morale.values().length - TWO) + 1);
        Optional<String> json = Optional.empty();
        Hamster hamster = Hamster.builder()
                .name(RandomStringUtils.randomAlphabetic(7))
                .bellyful(bellyful)
                .health(health)
                .morale(morale)
                .build();
        try {
            json = Optional.ofNullable(objectMapper.writeValueAsString(hamster));

        } catch (JsonProcessingException e) {
            log.error("Hamster can not be transformed into Json " + e.getMessage());
        }
        json.ifPresentOrElse(
                pubSubMessagePublisher::publish,
                () -> pubSubMessagePublisher.publish("Error publishing hamster"));
    }
}
