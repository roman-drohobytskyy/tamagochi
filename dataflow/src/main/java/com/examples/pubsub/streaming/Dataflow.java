package com.examples.pubsub.streaming;

import com.examples.pubsub.streaming.config.DataFlowOptions;
import com.examples.pubsub.streaming.service.DataFlowProcessor;
import java.io.IOException;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

public class Dataflow {

    public static void main(String[] args) throws IOException {

        DataFlowOptions options = PipelineOptionsFactory
            .fromArgs(args)
            .withValidation()
            .as(DataFlowOptions.class);
        DataFlowProcessor.runDataFlowJob(options);
    }
}
