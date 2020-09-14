package com.examples.pubsub.streaming.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class TamagochiDto implements Serializable {

    private String id;
    @NotNull
    private String name;
    @NotNull
    private String health;
    @NotNull
    private String bellyful;
    @NotNull
    private String morale;

}
