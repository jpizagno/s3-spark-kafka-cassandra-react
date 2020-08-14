package de.yelp.persister.persistence;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CassandraRepositoryTest {

    @Test
    public void testInsert() {
        String keyspace="logs";
        CassandraRepository repo = new CassandraRepository("localhost", 9042, keyspace);

        Date date = new Date();
        String modifiedDate= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);

        repo.persistLog("id-test-2", modifiedDate, "full log with id-test");
    }
}
