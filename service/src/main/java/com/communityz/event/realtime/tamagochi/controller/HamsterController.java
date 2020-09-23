package com.communityz.event.realtime.tamagochi.controller;

import com.communityz.event.realtime.tamagochi.dto.Hamster;
import com.communityz.event.realtime.tamagochi.service.HamsterPublisher;
import com.google.common.collect.Maps;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HamsterController {

    private final HamsterPublisher hamsterPublisher;
    private static final Map.Entry<String, String> STATUS_UP = Maps.immutableEntry("status", "UP");

    public HamsterController(HamsterPublisher hamsterPublisher) {
        this.hamsterPublisher = hamsterPublisher;
    }

    @GetMapping
    public Map.Entry<String, String> getHamster() {
        return STATUS_UP;
    }

    @GetMapping(value = "/hamsters/valid")
    public Hamster publishValidHamster() {
        log.info("Cron job publishing valid hamsters");
        return hamsterPublisher.publishValidHamsters();
    }

    @GetMapping(value = "/hamsters/invalid")
    public Hamster publishInvalidHamster() {
        log.info("Cron job publishing invalid hamsters");
        return hamsterPublisher.publishInvalidHamsters();
    }
}
