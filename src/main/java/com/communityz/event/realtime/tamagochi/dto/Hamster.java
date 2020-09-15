package com.communityz.event.realtime.tamagochi.dto;

import com.communityz.event.realtime.tamagochi.events.Bellyful;
import com.communityz.event.realtime.tamagochi.events.Health;
import com.communityz.event.realtime.tamagochi.events.Morale;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Hamster {

    @NotBlank
    private String name;
    @NotNull
    private Health health;
    @NotNull
    private Bellyful bellyful;
    @NotNull
    private Morale morale;

}
