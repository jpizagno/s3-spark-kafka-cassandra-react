package de.yelp.persister;

import de.yelp.persister.persistence.CassandraRepository;
import de.yelp.persister.streaming.LogStreamerConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.annotation.ExceptionProxy;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static String kafkaBootstrapServer;
    private static String kakfaInputTopic;
    private static String kakfaOutputTopic;
    private static long timeInPoll ;
    private static String groupId;
    private static String sessionTimeoutMillsec;
    private static String finishedKey;
    private static Integer numberScrapers;
    private static String repositoryUrl;
    private static String zookeeperQuorum;
    private static Integer zookeeperClientPort;
    private static String cassandraHost;  // "localhost" or "cassandra"
    private static String cassandraKeySpace;
    private static Integer cassandraPort;  // 9042

    public static void main(String[] args) {
        System.out.println("In Log-Persister main ... starting ... ");
        readSettingsFromEnvironment();

        waitForCassandra();

        LogStreamerConsumer consumer = new LogStreamerConsumer(kafkaBootstrapServer
                , zookeeperQuorum
                , zookeeperClientPort
                , kakfaInputTopic
                , timeInPoll
                , groupId
                , sessionTimeoutMillsec
                , cassandraHost
                , cassandraPort
                , cassandraKeySpace);
        consumer.consume();
    }

    private static boolean waitForCassandra() {
        System.out.println("Waiting for Cassandra");
        int numberAttmpts = 5;
        while(numberAttmpts > 0) {
            try {
                System.out.println("Waiting for Cassandra to be there numberAttmpts="+numberAttmpts);
                Thread.sleep(60000);
                CassandraRepository repo = new CassandraRepository(cassandraHost, cassandraPort, cassandraKeySpace);
                System.out.println("found Cassandra continuing  .. ");
                numberAttmpts = 0;
                return true;
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            numberAttmpts = numberAttmpts - 1;
        }
        System.out.println("ERROR: never found cassandra");
        return false;
    }

    private static void readSettingsFromEnvironment() {
        kafkaBootstrapServer = System.getenv("KAFKA_BOOTSTRAP_SERVER");
        zookeeperQuorum = System.getenv("ZOOKEEPER_QUORUM");
        zookeeperClientPort = new Integer( System.getenv("ZOOKEEPER_CLIENT_PORT") );
        kakfaInputTopic = System.getenv("KAFKA_INPUT_TOPIC");
        timeInPoll = new Long( System.getenv("TIME_IN_POLL") );
        groupId = System.getenv("GROUP_ID");
        sessionTimeoutMillsec = System.getenv("SESSION_TIMEOUT_MILLISEC");
        cassandraHost = System.getenv("CASSANDRA_HOST");
        cassandraKeySpace = System.getenv("CASSANDRA_KEYSPACE");
        cassandraPort = new Integer(System.getenv("CASSANDRA_PORT")); // 9042
    }

}
