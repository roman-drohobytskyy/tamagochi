package com.communityz.event.realtime.tamagochi.service;

import com.communityz.event.realtime.tamagochi.dto.Hamster;

public interface HamsterGenerator {

    Hamster generateValidHamster();

    Hamster generateInvalidHamster();

}
