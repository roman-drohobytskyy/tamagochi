package com.examples.pubsub.streaming.service;

import com.examples.pubsub.streaming.dto.TamagochiDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.bigquery.model.TableRow;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.repackaged.core.org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.beam.sdk.transforms.DoFn;

@Slf4j
public class BigQueryDataProcessor {

    public static class ToTableRow extends DoFn<TamagochiDto, TableRow> {

        @ProcessElement
        public void processElement(ProcessContext c) {
            try {
                TamagochiDto tamagochi = c.element();
                Gson gson = new Gson();
                ObjectMapper objectMapper = new ObjectMapper();
                TableRow outputRow = gson
                    .fromJson(objectMapper.writeValueAsString(tamagochi), TableRow.class);
                outputRow.set("timestamp", LocalDateTime.now().toString());
                c.output(outputRow);
                log.info("Writing to BigQuery {}", outputRow.get("name"));
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
