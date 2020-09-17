package com.examples.pubsub.streaming.service;

import com.examples.pubsub.streaming.config.DataFlowOptions;
import com.examples.pubsub.streaming.service.BigQueryDataProcessor.ToTableRow;
import com.examples.pubsub.streaming.dto.TamagochiDto;
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
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

@Slf4j
public class DataFlowProcessor {

    public static void runLocalValidatorDataFlow(DataFlowOptions options) throws IOException {
        GoogleCredentials credentials =
            ServiceAccountCredentials.fromStream(new FileInputStream(options.getKeyFilePath()))
                .createScoped(
                    Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
        options.setGcpCredential(credentials);

        Pipeline pipeline = Pipeline.create(options);

        // Read messages from PubSub
        PCollection<String> messages = readMessagesFromPubSub(options, pipeline);

        // Validate messages
        PCollection<TamagochiDto> validMessages =
            validatePubSubMessages(messages);

        // Write to BigQuery
        writeToBigQuery(options, validMessages);

//        // Write to Firestore
//        validMessages.apply("Write to Firestore",
//        ParDo.of(new FirestoreConnector(options.getKeyFilePath(),
//                options.getFirestoreCollection())));

        pipeline.run().waitUntilFinish();

    }

    private static PCollection<String> readMessagesFromPubSub(DataFlowOptions options,
        Pipeline pipeline) {
        String subscription =
            "projects/" + options.getProject() + "/subscriptions/" + options.getSubscription();
        log.info("Reading from subscription: " + subscription);
        return pipeline.apply("Get Hamsters from PubSub", PubsubIO.readStrings()
            .fromSubscription(subscription));
    }

    private static PCollection<TamagochiDto> validatePubSubMessages(PCollection<String> messages) {
        log.info("Validating PubSub messages");
        return messages.apply("Filter Valid Hamsters", ParDo.of(new JsonToTamagochiProcessor()));
    }

    private static void writeToBigQuery(DataFlowOptions options,
        PCollection<TamagochiDto> validMessages)
        throws JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
        JsonSchema schema = schemaGen.generateSchema(TamagochiDto.class);
        try {
            PCollection<TableRow> tableRow = validMessages
                .apply("Transform Hamsters to BQ Table rows", ParDo.of(new ToTableRow()));
            tableRow.apply("Write Hamsters To BQ",
                BigQueryIO.writeTableRows()
                    .to(String.format("%s.%s", options.getBqDataSet(), options.getBqTable()))
                    .withJsonSchema(schema.toString())
                    .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));
            log.info("BigQuery writing stage initialized");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}