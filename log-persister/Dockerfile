FROM openjdk:8

# install Maven
RUN apt-get update -y
RUN apt-get install maven -y

ENV KAFKA_BOOTSTRAP_SERVER=kafka:9092
ENV ZOOKEEPER_QUORUM=kafka
ENV ZOOKEEPER_CLIENT_PORT=2188
ENV KAFKA_INPUT_TOPIC=log-streamer-out
ENV TIME_IN_POLL=100
ENV GROUP_ID=group-id-persister-1
ENV SESSION_TIMEOUT_MILLISEC=30000
ENV CASSANDRA_HOST=cassandra
ENV CASSANDRA_KEYSPACE=logs
ENV CASSANDRA_PORT=9042

# create app and install code
RUN mkdir /usr/app/
WORKDIR /usr/app/
COPY . /usr/app/
RUN mvn clean compile -q assembly:single

# run code
CMD ["java", "-jar", "/usr/app/target/log-persister-1.0-SNAPSHOT-jar-with-dependencies.jar"]