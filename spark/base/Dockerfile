FROM openjdk:8

RUN apt-get update \
 && apt-get install -y locales \
 && dpkg-reconfigure -f noninteractive locales \
 && locale-gen C.UTF-8 \
 && /usr/sbin/update-locale LANG=C.UTF-8 \
 && echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen \
 && locale-gen \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /opt/huginn/software/ \
              /opt/huginn/binaries

# Users with other locales should set this in their derivative image
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

RUN apt-get update \
 && apt-get install -y curl unzip wget \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

## SPARK
################################################################################
# Download and install Spark
################################################################################
WORKDIR /opt/huginn/binaries
ENV SPARK_VERSION 2.4.6
ENV SPARK_PACKAGE spark-${SPARK_VERSION}-bin-hadoop2.6.tgz
ENV FULL_DOWN_PATH /opt/huginn/software/${SPARK_PACKAGE} 
RUN wget -O ${FULL_DOWN_PATH} "https://www.apache.org/dyn/mirrors/mirrors.cgi?action=download&filename=spark/spark-${SPARK_VERSION}/${SPARK_PACKAGE}"
WORKDIR /opt/huginn/software
RUN tar -xzvf ${SPARK_PACKAGE}
RUN ln -s spark-${SPARK_VERSION}-bin-hadoop2.6 spark
ENV SPARK_HOME /opt/huginn/software/spark
ENV PATH $PATH:${SPARK_HOME}/bin

WORKDIR $SPARK_HOME
#CMD ["bin/spark-class", "org.apache.spark.deploy.master.Master"]