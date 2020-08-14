#!/bin/bash

DATE=`/bin/date '+%Y%m%d_%H%M%S'`


echo "################################################################################"
echo "# Starting container (${DATE})"
echo "################################################################################"

#cd /opt/huginn/software/kafka-manager

echo "Removing PID file if exsits from previous runs"
rm RUNNING_PID

echo "Waiting infinity to ensure ZK and Kafka are up"
i="0"
rm  /tmp/telnetresult.tmp
# will loop forever until 
while [ $i -lt 1 ]
do
    echo "trying to find kafka:9092 "
    sleep 2 | telnet kafka 9092 | tee -a  -i /tmp/telnetresult.tmp
    # if fail, then telnetresult has "Trying 127.0.0.1..."
    # if success, then /tmp/telnetresult.tmp has 
    #       "Trying 127.0.0.1..."
    #       "Connected to localhost."
    #       "Escape character is '^]'."
    # So search file for "Connected"
    if grep -q Connected /tmp/telnetresult.tmp; then
        echo "SUCCESS!  found kafka"
        i="10"
    else 
        echo "FAIL! still waiting for Kakfa to come up"
    fi

    rm  /tmp/telnetresult.tmp
done
echo "Found Kafka, contining..."

echo "Starting kafka-manager and logging into kafka-manager.log"
/kafka-manager-1.3.0.4/bin/kafka-manager >& kafka-manager.log &

echo "Waiting 30 seconds that kafka-manager becomes ready"
sleep 30


# THIS IS THE FULL LIST OF OPTIONS (kafka-manager 1.3.3.13)
# name, zkHosts, kafkaVersion, securityProtocol is MANDATORY (no error shows up if not specified!)
# activeOffsetCacheEnabled and pollConsumers for life updates about offsets


# --------------------------------------------------------------------------------
#activeOffsetCacheEnabled	true
#jmxPass	
#jmxUser	
#kafkaVersion	0.10.2.1
#name	asdfdsaf
#pollConsumers	true
#securityProtocol	PLAINTEXT
#tuning.brokerViewThreadPoolQueueSize	1000
#tuning.brokerViewThreadPoolSize	8
#tuning.brokerViewUpdatePeriodSeconds	30
#tuning.clusterManagerThreadPoolQueueSize	100
#tuning.clusterManagerThreadPoolSize	2
#tuning.kafkaAdminClientThreadPoolQueueSize	1000
#tuning.kafkaAdminClientThreadPoolSize	8
#tuning.kafkaCommandThreadPoolQueueSize	100
#tuning.kafkaCommandThreadPoolSize	2
#tuning.logkafkaCommandThreadPoolQueueSize	100
#tuning.logkafkaCommandThreadPoolSize	2
#tuning.logkafkaUpdatePeriodSeconds	30
#tuning.offsetCacheThreadPoolQueueSize	1000
#tuning.offsetCacheThreadPoolSize	8
#tuning.partitionOffsetCacheTimeoutSecs	5
#zkHosts	kafka:2188
# --------------------------------------------------------------------------------


echo "Adding Demo cluster"
curl localhost:9000/clusters \
    --data-urlencode "name=demo" \
    --data-urlencode "zkHosts=kafka:2188" \
    --data-urlencode "kafkaVersion=0.10.2.1" \
    --data-urlencode "securityProtocol=PLAINTEXT" \
    --data-urlencode "activeOffsetCacheEnabled=true" \
    --data-urlencode "pollConsumers=true" \
    -X POST

echo "Container started"

sleep infinity