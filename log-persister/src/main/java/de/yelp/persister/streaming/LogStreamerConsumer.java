package de.yelp.persister.streaming;

import de.yelp.persister.persistence.CassandraRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;


public class LogStreamerConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogStreamerConsumer.class);

    private final Properties kafkaProperties;

    protected KafkaConsumer<String, String> kafkaConsumer;

    private final Properties props;
    private String kakfaInputTopic;
    private String zookeeperQuorum;
    private Integer zookeeperClientPort;
    private final long timeInPoll;
    private CassandraRepository repo;

    public LogStreamerConsumer(String kafkaBootstrapServer
            , String zookeeperQuorum
            , Integer zookeeperClientPort
            , String kakfaInputTopic
            , long timeInPoll  // default 100
            , String groupId
            , String sessionTimeoutMillsec
            , String cassandraHost
            , Integer cassandraPort
            , String keySpace) {

        this.kakfaInputTopic = kakfaInputTopic;
        this.zookeeperQuorum = zookeeperQuorum;
        this.zookeeperClientPort = zookeeperClientPort;
        this.timeInPoll = timeInPoll;

        props = new Properties();
        props.put("bootstrap.servers", kafkaBootstrapServer);
        //props.put("zookeeper.connect", zookeeperQuorum + ":" + zookeeperClientPort);
        props.put("group.id", groupId);  // default = "test-consumer-group"
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "earliest");
        props.put("session.timeout.ms", sessionTimeoutMillsec);  //  "30000" If no heartbeats are received by the broker before the expiration of this session timeout, then the broker will remove this consumer from the group and initiate a rebalance.
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        kafkaProperties = new Properties();
        kafkaProperties.put("bootstrap.servers", kafkaBootstrapServer);
        kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        repo = new CassandraRepository(cassandraHost, cassandraPort, keySpace);

        LOGGER.info("constructor finished");
    }

    /**
     * This method will subscribe to the topic, given params in constructor
     */
    public void consume() {

        if (kafkaConsumer == null) { // may not be null for testing, mocking
            LOGGER.info("properties loaded");
            kafkaConsumer = new KafkaConsumer<>(props);
        }

        kafkaConsumer.subscribe(Arrays.asList(kakfaInputTopic));

        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(timeInPoll);
            for (ConsumerRecord<String, String> record : records) {
                persisteLogMessage(record);
            }
        }
    }

    /**
     * Assumes that the record.value() is a line in a Log message from S3.  Should look something like this:
     * ******** 1003 hello world ******* 7733547c288eb867664cba21791c61b48ef35e0696c17cca5f6ed114b567e86d graphs.huginns-news.com [13/Apr/2020:09:56:18 +0000] 87.161.210.2 7733547c288eb867664cba21791c61b48ef35e0696c17cca5f6ed114b567e86d C3311B6D6859FD75 REST.HEAD.BUCKET - "HEAD /graphs.huginns-news.com HTTP/1.1" 200 - - - 10 10 "-" "S3Console/0.4, aws-internal/3 aws-sdk-java/1.11.753 Linux/4.9.184-0.1.ac.235.83.329.metal1.x86_64 OpenJDK_64-Bit_Server_VM/25.242-b08 java/1.8.0_242 vendor/Oracle_Corporation" - MS4/8T62s3sUUsX1aK5/KLI8mpUbDqrJwbSpg2MPNTPXRAF/d3Gpe+JFxq3obwIdpwJvDV7XzW4= SigV4 ECDHE-RSA-AES128-SHA AuthHeader s3.eu-central-1.amazonaws.com TLSv1.2
     * but it doesn't matter
     * @param record
     */
    private void persisteLogMessage(ConsumerRecord<String, String> record) {
        LOGGER.info("processing message = {}", record.value());

        String fullLog = record.value();
        Date date = new Date();
        String modifiedDate= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
        String id = fullLog.split(" ")[5];

        repo.persistLog(id, modifiedDate, fullLog);
        LOGGER.info("processED message = {}", record.value());
    }

}
