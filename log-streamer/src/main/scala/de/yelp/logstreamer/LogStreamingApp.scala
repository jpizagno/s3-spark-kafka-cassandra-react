package de.yelp.logstreamer

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming._

import java.util.Properties
import org.apache.kafka.clients.producer._


/**
  * Use this when submitting the app to a cluster with spark-submit
  * */
object LogStreamingApp extends App {
  val (myAccessKey, mySecretKey, bucketName) = (args(0), args(1), args(2))
  println(" ************ read bucketName = " + bucketName)

  // spark-submit command should supply all necessary config elements
  val conf = new SparkConf()
    .setAppName("Simple Application").setMaster("local[*]")
  Runner.run(conf, myAccessKey, mySecretKey, bucketName)
}

object Runner extends Serializable {
  /**
   *
   * To submit see spark-submit-command.txt
   *
   * @param conf  val conf = new SparkConf().setAppName("Simple Application").setMaster("local[*]")
   * @param myAccessKey
   * @param mySecretKey
   * @param bucketName  i.e. "s3n://path to bucket"
   */
  def run(conf: SparkConf, myAccessKey: String, mySecretKey: String, bucketName: String): Unit = {
    val sc = new SparkContext(conf)

    val hadoopConf=sc.hadoopConfiguration;
    hadoopConf.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
    hadoopConf.set("fs.s3a.access.key",myAccessKey)
    hadoopConf.set("fs.s3a.secret.key",mySecretKey)
    hadoopConf.set("fs.s3a.aws.credentials.provider","org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")

    val ssc = new org.apache.spark.streaming.StreamingContext(
      sc,Seconds(60))
    val newFiles = ssc.textFileStream(bucketName)
    newFiles.print()


    val kafkaBootstrapServer: String = "kafka:9092"
    val topic: String = "log-streamer-out"

    // Scala kafka
    val  props = new Properties()
    props.put("bootstrap.servers", kafkaBootstrapServer)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    newFiles.foreachRDD((rdd) => rdd.foreach((line) => {
      val producer = new KafkaProducer[String, String](props)
      val record = new ProducerRecord(topic, "key", line)
      producer.send(record)
    }))

    ssc.start()             // Start the computation
    ssc.awaitTermination()  // Wait for the computation to terminate
  }

}
