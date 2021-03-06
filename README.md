This project monitors a S3 bucket for new log files, then persists and displays the data.

A Spark job in log-streamer/ will monitor an S3 bucket, when a new file comes, with send the line-text into a kafka topic="log-streamer-out".  

Kafka is in kafka/ , and there is a manager in kafka-manger.  A kafka proxy server is in kafka-proxy-ws/ which provides a server for the websocket in the frontend/ app.  

The log lines, in the Kafka topic="log-streamer-out", are persisted to Cassandra (cassandra/ ) by the app log-persister/.

Nothing happends until a file is uploaded to the S3 bucket.  

### UML Data Flow
![alt text](https://github.com/jpizagno/s3-spark-kafka-cassandra-react/blob/master/UML/uml.png?raw=true)


### Build and Run
One needs to build the spark-base image:

```bash
cd spark/base/
docker build -t spark-base .
cd ../../
```

Then start docker compose in the background:
```bash
nohup ./docker-compose-run.sh > docker-compose-run.log 2>&1 &
```
*wait*:  Building all the Docker images and running apps will take about 20 Minutes.  

Then submit Spark Job:

*Note*:  need to have Java and [sbt](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html) installed:  
```bash
nohup ./start_log_streamer.sh {AWS-key}  {AWS-secret} {S3 bucket name like: s3a://huginns-news-logs/} > start_log_streamer.log 2>&1 &
```

When a new file is uploaded/written to the S3 bucket, it will get picked up by the Spark job, produced into Kafka, pushed to the ReactJS app, and persisted to Cassandra.

### Monitor Progress

Kafka-Manager  (You may have to add the Cluster manually) :
localhost:9001

Spark Job UI:
localhost:4040

Frontent App:
localhost:3000

or just look in either *log file.   

### See some action.
Upload a file to the S3 bucket, and wait about 1 minute, and Frontend App (localhost:3000) should contents of that file.