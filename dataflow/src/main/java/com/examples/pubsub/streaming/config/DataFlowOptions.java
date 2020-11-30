package com.examples.pubsub.streaming.config;

import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.StreamingOptions;
import org.apache.beam.sdk.options.Validation.Required;

public interface DataFlowOptions extends StreamingOptions, GcpOptions {

    @Description("The Cloud Pub/Sub subscription to read from.")
    @Default.String("dataflow-job")
    String getSubscription();

    void setSubscription(String value);

    @Description("GCP service account key location")
    @Default.String("GCP service account key location in .json format")
    String getKeyFilePath();

    void setKeyFilePath(String keyFilePath);

    @Description("BigQuery dataset name")
    @Default.String("dataflow")
    String getBqDataSet();

    void setBqDataSet(String dataSet);

    @Description("BigQuery table name")
    @Default.String("dataflow")
    String getBqTable();

    void setBqTable(String table);

    @Description("Path of the output file including its filename prefix.")
    @Required
    String getOutput();
    void setOutput(String value);

}
