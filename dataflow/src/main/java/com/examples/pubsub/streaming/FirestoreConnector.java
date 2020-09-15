package com.examples.pubsub.streaming;

import com.examples.pubsub.streaming.dto.TamagochiDto;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirestoreConnector extends DoFn<TamagochiDto, TamagochiDto> {

    private final static Logger LOG = LoggerFactory.getLogger(JsonToTamagochiDto.class);

    private final String filePath;
    private final String firestoreCollection;

    public FirestoreConnector(String filePath, String firestoreCollection) {
        this.filePath = filePath;
        this.firestoreCollection = firestoreCollection;
    }

    @ProcessElement
    public void processElement(ProcessContext c) throws Exception {
        Firestore firestore = FirestoreOptions.getDefaultInstance()
                .getService();
        try {
            DocumentReference docRef = firestore.collection(firestoreCollection).document();
            docRef.set(c.element());
            LOG.info("Saved to Firestore");
        } catch (Exception e) {
            LOG.error("Failed to save user to Firestore");
            LOG.error(e.getMessage());
        }
    }
}
