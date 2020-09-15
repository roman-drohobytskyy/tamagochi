package com.communityz.event.realtime.tamagochi.service;

import com.communityz.event.realtime.tamagochi.dto.Hamster;
import com.communityz.event.realtime.tamagochi.publisher.PubSubMessagePublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScheduledTask {

    private final PubSubMessagePublisher pubSubMessagePublisher;
    private final ObjectMapper objectMapper;
    private final HamsterGenerator hamsterGenerator;

    public ScheduledTask(PubSubMessagePublisher pubSubMessagePublisher, ObjectMapper objectMapper,
        HamsterGenerator hamsterGenerator) {
        this.pubSubMessagePublisher = pubSubMessagePublisher;
        this.objectMapper = objectMapper;
        this.hamsterGenerator = hamsterGenerator;
    }

    @Scheduled(fixedRateString = "${scheduling.hamsters.valid}")
    public void publishValidHamsters() {
        Hamster hamster = hamsterGenerator.generateValidHamster();
        publish(hamster);
    }

    @Scheduled(fixedRateString = "${scheduling.hamsters.invalid}", initialDelay = 1000)
    public void publishInvalidHamsters() {
        Hamster hamster = hamsterGenerator.generateInvalidHamster();
        publish(hamster);
    }

    private void publish(Hamster hamster) {
        Optional<String> json = Optional.empty();
        try {
            json = Optional.ofNullable(objectMapper.writeValueAsString(hamster));
        } catch (JsonProcessingException e) {
            log.error("Hamster can not be transformed into Json" + e.getMessage());
        }
        json.ifPresentOrElse(
            h -> {
                pubSubMessagePublisher.publish(h);
                log.info("Hamster '{}' has been successfully published", hamster.getName());
            },
            () -> pubSubMessagePublisher
                .publish(String.format("Error publishing the hamster '%s'", hamster.getName())));
    }
}
