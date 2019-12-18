package tao

import akka.actor.Actor
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Attributes, Materializer}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

class KafkaProducerActor extends Actor{

  override def receive: Receive = {
    case message : String =>
    val actorSystem = context.system
    implicit val actorMaterializer: Materializer = ActorMaterializer()
    val config = actorSystem.settings.config.getConfig("akka.kafka.producer")

    val myTopic = config.getString("myTopic")
    val SASL_JAAS_CONFIG = config.getString("sasl.jaas.config")
    val SASL_MECHANISM = config.getString("sasl.mechanism")
    val SECURITY_PROTOCOL = config.getString("security.protocol")
    val SSL_PROTOCOL = config.getString("ssl.protocol")

    println(myTopic)
    val producerSettings: ProducerSettings[String, String] =  ProducerSettings(actorSystem, new StringSerializer, new StringSerializer)
      .withProperties(Map[String, String] (
        "sasl.jaas.config" -> SASL_JAAS_CONFIG,
        "sasl.mechanism" -> SASL_MECHANISM,
        "security.protocol" -> SECURITY_PROTOCOL,
        "ssl.protocol" -> SSL_PROTOCOL
      ))
      Source.single(message)
        .log("a test")
        .addAttributes( Attributes.logLevels(
          onElement = Attributes.LogLevels.Info,
          onFailure = Attributes.LogLevels.Error,
          onFinish = Attributes.LogLevels.Info )
        )
        .map(value => new ProducerRecord[String, String](myTopic, value))
        .runWith(Producer.plainSink(producerSettings))
    //     .runWith(Producer.plainSink(producerSettings))
  }
}
