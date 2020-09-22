package com.examples.pubsub.streaming.service;

import com.examples.pubsub.streaming.dto.TamagochiDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.repackaged.core.org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.beam.sdk.transforms.DoFn;

@Slf4j
public class CloudStorageDataProcessor {

    public static class ToString extends DoFn<TamagochiDto, String> {


        @ProcessElement
        public void processElement(ProcessContext c) {
            try {
                TamagochiDto tamagochi = c.element();
                ObjectMapper objectMapper = new ObjectMapper();
                c.output(objectMapper.writeValueAsString(tamagochi));
                log.info("Writing to Cloud Storage {}", tamagochi.getName());
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
