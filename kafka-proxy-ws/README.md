
https://medium.com/@lahirugmg/managed-websocket-api-for-kafka-with-wso2-api-manager-3e17f6e9a121


### to build and run:
```
./buld.sh

./run.sh
```

### to change server settings in lines 9-10 of samples/server.js:

```
    wsPort: 9998, # Other apps' websocket must listen to this port
    kafka: 'kafka:9092/', # other apps' websocket must listen to this address
     # localhost - WebSocket('ws;//127.0.0.1:998')
```

Current websocket implemenation with this setup is:
```
 var kafkaSocket = new WebSocket('ws://kafkaproxy:9998/?topic=huginn_analyzed&consumerGroup=group1');

```

