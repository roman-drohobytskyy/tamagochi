package com.examples.pubsub.streaming;

import org.apache.beam.sdk.options.PipelineOptionsFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        DataFlowOptions options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(DataFlowOptions.class);
        DataFlowProcessor.runLocalValidatorDataFlow(options);
    }
}
