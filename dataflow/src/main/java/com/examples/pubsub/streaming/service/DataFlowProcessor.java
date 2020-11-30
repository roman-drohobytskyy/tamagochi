package com.examples.pubsub.streaming.service;

import com.examples.pubsub.streaming.config.DataFlowOptions;
import com.examples.pubsub.streaming.dto.TamagochiDto;
import com.examples.pubsub.streaming.service.BigQueryDataProcessor.ToTableRow;
import com.examples.pubsub.streaming.service.CloudStorageDataProcessor.ToString;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.google.api.services.bigquery.model.TableRow;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.examples.common.WriteOneFilePerWindow;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;

@Slf4j
public class DataFlowProcessor {

    public static void runDataFlowJob(DataFlowOptions options) throws IOException {
        setCredentials(options);

        Pipeline pipeline = Pipeline.create(options);

        // Read messages from PubSub
        PCollection<String> messages = readMessagesFromPubSub(options, pipeline);

        // Validate messages
        PCollection<TamagochiDto> validMessages = validatePubSubMessages(messages);

        // Write to BigQuery
        writeToBigQuery(options, validMessages);

        // Write to Cloud Storage
         writeToCloudStorage(options, validMessages);

        pipeline.run().waitUntilFinish();

    }

    private static void setCredentials(DataFlowOptions options) throws IOException {
        GoogleCredentials credentials =
            ServiceAccountCredentials.fromStream(new FileInputStream(options.getKeyFilePath()))
                .createScoped(
                    Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
        options.setGcpCredential(credentials);
    }

    private static PCollection<String> readMessagesFromPubSub(DataFlowOptions options,
        Pipeline pipeline) {
        String subscription =
            "projects/" + options.getProject() + "/subscriptions/" + options.getSubscription();
        log.info("Reading from subscription: " + subscription);
        return pipeline.apply("Read Messages from Pub/Sub", PubsubIO.readStrings()
            .fromSubscription(subscription));
    }

    private static PCollection<TamagochiDto> validatePubSubMessages(PCollection<String> messages) {
        log.info("Validating PubSub messages");
        return messages.apply("Filter Valid Messages", ParDo.of(new JsonToTamagochiProcessor()));
    }

    private static void writeToBigQuery(DataFlowOptions options,
        PCollection<TamagochiDto> validMessages)
        throws JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
        JsonSchema schema = schemaGen.generateSchema(TamagochiDto.class);
        try {
            PCollection<TableRow> tableRow = validMessages
                .apply("Convert to BigQuery Table Row", ParDo.of(new ToTableRow()));
            tableRow.apply("Write To BigQuery",
                BigQueryIO.writeTableRows()
                    .to(String.format("%s.%s", options.getBqDataSet(), options.getBqTable()))
                    .withJsonSchema(schema.toString())
                    .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));
            log.info("BigQuery writing stage initialized");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static void writeToCloudStorage(DataFlowOptions options,
        PCollection<TamagochiDto> validMessages) {
        PCollection<String> tableRow = validMessages
            .apply("Transform Hamsters to Storage Table rows", ParDo.of(new ToString()))
            .apply(Window.into(FixedWindows.of(Duration.standardSeconds(1))));
        log.info("Cloud Storage writing stage initialized");
        tableRow.apply("Write to Cloud Storage", new WriteOneFilePerWindow(options.getOutput(), 1));
    }

}