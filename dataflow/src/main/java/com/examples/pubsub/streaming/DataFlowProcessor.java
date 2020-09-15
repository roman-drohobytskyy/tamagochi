package com.examples.pubsub.streaming;

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
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataFlowProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(DataFlowProcessor.class);

    static void runLocalValidatorDataFlow(DataFlowOptions options) throws IOException {
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
            messages.apply("FilterValidMessages", ParDo.of(new JsonToTamagochiDto()));
//
//        // Write to BigQuery
//        writeToBigQuery(options, validMessages);
//
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
        LOG.info("Reading from subscription: " + subscription);
        return pipeline.apply("GetPubSub", PubsubIO.readStrings()
            .fromSubscription(subscription));
    }

    private static void writeToBigQuery(DataFlowOptions options,
        PCollection<TamagochiDto> validMessages)
        throws JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
        JsonSchema schema = schemaGen.generateSchema(TamagochiDto.class);
        try {
            PCollection<TableRow> tableRow = validMessages
                .apply("ToTableRow", ParDo.of(new PrepData.ToTableRow()));
            tableRow.apply("WriteToBQ",
                BigQueryIO.writeTableRows()
                    .to(String.format("%s.%s", options.getBqDataSet(), options.getBqTable()))
                    .withJsonSchema(schema.toString())
                    .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));
            LOG.info("Writing completed");
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}