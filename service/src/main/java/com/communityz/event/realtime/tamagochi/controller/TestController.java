package com.communityz.event.realtime.tamagochi.controller;

import com.communityz.event.realtime.tamagochi.dto.Hamster;
import com.communityz.event.realtime.tamagochi.service.HamsterGenerator;
import com.communityz.event.realtime.tamagochi.service.ScheduledTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    private final HamsterGenerator hamsterGenerator;
    private final ScheduledTask scheduledTask;

    public TestController(
        HamsterGenerator hamsterGenerator,
        ScheduledTask scheduledTask) {
        this.hamsterGenerator = hamsterGenerator;
        this.scheduledTask = scheduledTask;
    }

    @GetMapping
    public Hamster getHamster() {
        Hamster hamster = hamsterGenerator.generateValidHamster();
        log.info("Test hamster - {}", hamster);
        return hamster;
    }

    @GetMapping(value = "/hamsters/valid")
    public Hamster publishValidHamster() {
        log.info("Cron job publishing valid hamsters");
        return scheduledTask.publishValidHamsters();
    }

    @GetMapping(value = "/hamsters/invalid")
    public Hamster publishInvalidHamster() {
        log.info("Cron job publishing invalid hamsters");
        return scheduledTask.publishInvalidHamsters();
    }
}
