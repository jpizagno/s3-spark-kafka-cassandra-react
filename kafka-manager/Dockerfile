FROM sheepkiller/kafka-manager:1.3.0.4

################################################################################
# Telnet
################################################################################
RUN yum install -y telnet

RUN mkdir -p /opt/huginn/binaries
RUN mkdir -p /opt/huginn/configuration
RUN mkdir -p /opt/huginn/software/

################################################################################
# Install Huginn Software
################################################################################
COPY startup/start_services.sh /opt/huginn/

#################################################################################
## Start kafka-manager and configure cluster
#################################################################################
ENTRYPOINT /opt/huginn/start_services.sh