FROM openjdk:8

################################################################################
# Install essential packages:
################################################################################
RUN apt-get -y update && \
    apt-get -y upgrade && \ 
    apt-get install -y \
    wget \
    telnet \
    nmap \
    unzip \
    curl

RUN mkdir -p /opt/huginn/binaries
RUN mkdir -p /opt/huginn/configuration
RUN mkdir -p /opt/huginn/software/

ADD configuration /opt/huginn/configuration
COPY startup/start_services.sh /opt/huginn/


################################################################################
# Install Kafka and symlink
################################################################################
WORKDIR /opt/huginn/binaries
RUN if [ ! -e kafka_2.11-0.10.2.1.tgz ]; then wget https://archive.apache.org/dist/kafka/0.10.2.1/kafka_2.11-0.10.2.1.tgz ; fi
RUN tar -xzvf kafka_2.11-0.10.2.1.tgz -C /opt/huginn/software/     
WORKDIR /opt/huginn/software
RUN ln -s kafka_2.11-0.10.2.1 kafka

################################################################################
# Configure Kafka
################################################################################
ENV KAFKA_OPTS="-Djava.net.preferIPv4Stack=True"
RUN cp /opt/huginn/configuration/server.properties ./kafka/config
RUN cp /opt/huginn/configuration/zookeeper.properties ./kafka/config

################################################################################
# Start Kafka
################################################################################
ENTRYPOINT /opt/huginn/start_services.sh