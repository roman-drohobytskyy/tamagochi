package com.examples.pubsub.streaming;

import com.examples.pubsub.streaming.dto.TamagochiDto;
import com.examples.pubsub.streaming.dto.TamagochiDtoValidator;
import java.util.UUID;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.vendor.grpc.v1p21p0.com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonToTamagochiDto extends DoFn<String, TamagochiDto> {

    private final static Logger LOG = LoggerFactory.getLogger(JsonToTamagochiDto.class);

    @ProcessElement
    public void processElement(ProcessContext c) {
        String entityJson = c.element();
        Gson gson = new Gson();

        TamagochiDto tamagochiDto;

        try {
            tamagochiDto = gson.fromJson(entityJson, TamagochiDto.class);
            if (TamagochiDtoValidator.isUserDtoValid(tamagochiDto)) {
                tamagochiDto.setId(UUID.randomUUID().toString());
                c.output(tamagochiDto);
                LOG.info("Processing Hamster - '{}'", tamagochiDto.getName());
            } else {
                LOG.info(tamagochiDto.toString() + " is not valid");
            }
        } catch (Exception e) {
            LOG.info("Cast json to UserDto was failed:" + e.getMessage());
            e.printStackTrace();
        }

    }
}