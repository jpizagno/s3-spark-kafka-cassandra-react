#!/usr/bin/env bash

rm -rf cassandra_out

docker-compose -f docker-compose.yml up --force-recreate  