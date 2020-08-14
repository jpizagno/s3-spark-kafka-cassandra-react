#!/usr/bin/env bash

# get AWS keys and S3 bucket
[[ -z "$1" ]] && { echo "Parameter 1, AWSAccessKeyId (AK....A) is empty" ; exit 3; }
[[ -z "$2" ]] && { echo "Parameter 2, AWSSecretKey (AK...QA) is empty" ; exit 4; }
[[ -z "$3" ]] && { echo "Parameter 3, s3 bucket ( s3a://huginns-news-logs/ )  is empty" ; exit 5; }
AWS_KEY="$1"
echo "read from command line AWS_KEY="$AWS_KEY
AWS_SECRET="$2"
S3_BUCKET="$3"


# build log_streamer
cd log-streamer/
rm -rf ./target/scala-2.11/*
sbt assembly
cd ..

# get Spark-Master container id
SPARK_MASTER_CONTAINER_ID="$(docker ps -aqf "name=master")"

# copy log-streamer-assembly-0.0.1.jar into Spark Master
docker cp log-streamer/target/scala-2.11/log-streamer-assembly-0.0.1.jar $SPARK_MASTER_CONTAINER_ID:/tmp/

# spark-submit
docker exec  $SPARK_MASTER_CONTAINER_ID  spark-submit --class de.yelp.logstreamer.LogStreamingApp \
    --name log_streaming_app \
    --master local \
    --packages com.amazonaws:aws-java-sdk:1.7.4,org.apache.hadoop:hadoop-aws:2.7.7 \
    --conf spark.hadoop.fs.s3a.endpoint=s3.eu-central-1.amazonaws.com \
    --conf spark.executor.extraJavaOptions=-Dcom.amazonaws.services.s3.enableV4=true \
    --conf spark.driver.extraJavaOptions=-Dcom.amazonaws.services.s3.enableV4=true  \
    /tmp/log-streamer-assembly-0.0.1.jar \
    $AWS_KEY \
    $AWS_SECRET \
    $S3_BUCKET &

echo "started Spark job via spark-submit"
