#!/bin/bash

DATE=`/bin/date '+%Y%m%d_%H%M%S'`


echo "################################################################################"
echo "# Starting container (${DATE})"
echo "################################################################################"

cd /opt/huginn/software/kafka

echo "Starting internal Zookeeper@2181 as daemon"
./bin/zookeeper-server-start.sh -daemon ./config/zookeeper.properties

echo "Waiting 10 seconds"
sleep 10

echo "Starting Kafka@9092 as daemon"
./bin/kafka-server-start.sh -daemon ./config/server.properties

echo "Container started"

sleep infinity