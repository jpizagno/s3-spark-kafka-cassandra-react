package de.yelp.persister.persistence;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

public class CassandraRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraRepository.class);

    private Session session;

    public CassandraRepository(String host, Integer port, String keyspace) {
        Collection<InetSocketAddress> addresses = new ArrayList<>();
        addresses.add(new InetSocketAddress(host, port));
        Cluster cluster = Cluster.builder().addContactPointsWithPorts(addresses).build();
        session = cluster.connect(keyspace);
    }

    public void persistLog(String id, String date, String fullLog) {
        LOGGER.info("trying to insert id="+id);
        Timestamp dateTs = Timestamp.valueOf(date);
        Insert insertInto = QueryBuilder.insertInto("log")
                .value("id", id)
                .value("date", dateTs)
                .value("full_log", fullLog);
        session.execute(insertInto);
        LOGGER.info("insertED id="+id);
    }
}
