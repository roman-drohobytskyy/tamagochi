package com.communityz.event.realtime.tamagochi.service;

import com.communityz.event.realtime.tamagochi.dto.Hamster;
import com.communityz.event.realtime.tamagochi.events.Bellyful;
import com.communityz.event.realtime.tamagochi.events.Health;
import com.communityz.event.realtime.tamagochi.events.Morale;
import com.communityz.event.realtime.tamagochi.publisher.PubSubMessagePublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.SecureRandom;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScheduledTask {

    private final PubSubMessagePublisher pubSubMessagePublisher;
    private final ObjectMapper objectMapper;

    public ScheduledTask(PubSubMessagePublisher pubSubMessagePublisher, ObjectMapper objectMapper) {
        this.pubSubMessagePublisher = pubSubMessagePublisher;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        Optional<String> json = Optional.empty();
        Hamster hamster = Hamster.builder()
          .name(RandomStringUtils.randomAlphabetic(7))
          .bellyful(getRandomEnumValue(Bellyful.class))
          .health(getRandomEnumValue(Health.class))
          .morale(getRandomEnumValue(Morale.class))
          .build();
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
          () -> pubSubMessagePublisher.publish(String.format("Error publishing the hamster '%s'", hamster.getName())));
    }

    public static <T extends Enum<?>> T getRandomEnumValue(Class<T> clazz) {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[number];
    }
}
