A highly configurable Google Cloud Dataflow pipeline that writes data into a Google Big Query table from Pub/Sub

Compile this Dataflow pipeline into a runnable Jar (pubsub-to-bq.jar). Then call the jar with following parameters:

```bash
java.exe -jar "C:\Jars\pubsub-streaming.jar" 
    --runner=DataflowRunner  (optional - specify this in case running on GCP)
    --project='your project Id'
    --region='region'
    --tempLocation='storage bucket with saging folder for dataflow job'
    --subscription='Topic subscription to read messages from'
    --keyFilePath='GCP service account key location'
    --bqDataSet='BigQuery dataset name'
    --bqTable='BigQuery table name'
    --firestoreCollection='Firestore Collection name'
```
N.B! In case you are running this beauty in your local environment you should provide environment variable: 
```bash
GOOGLE_APPLICATION_CREDENTIALS=GCP service account key location in .json format
```

- Only provide .json key files for GCP.
- Pipeline can support queues with batched messages.

Example for program parameters in run/debug Configurations:
```
--project=neural-medium-289511
--tempLocation=gs://hamster-dataflow/staging
--subscription=hamster
--keyFilePath=C:\hamster\dataflow-key.json
--bqDataSet=hamsters
--bqTable=hamsters
--output=gs://hamster-dataflow/hamsters
```