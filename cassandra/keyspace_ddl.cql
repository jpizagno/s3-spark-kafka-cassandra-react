DROP KEYSPACE IF EXISTS logs;

CREATE KEYSPACE logs WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};

USE logs;

CREATE TABLE log (
  id text,
  date timestamp,
  full_log text, 
  PRIMARY KEY ((id, date))
);