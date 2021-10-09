package com.examples.pubsub.streaming.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
