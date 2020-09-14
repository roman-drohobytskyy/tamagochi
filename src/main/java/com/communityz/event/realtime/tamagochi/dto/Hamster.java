package com.communityz.event.realtime.tamagochi.dto;

import com.communityz.event.realtime.tamagochi.events.Bellyful;
import com.communityz.event.realtime.tamagochi.events.Health;
import com.communityz.event.realtime.tamagochi.events.Morale;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Hamster {

    private String name;
    private Health health;
    private Bellyful bellyful;
    private Morale morale;

}
