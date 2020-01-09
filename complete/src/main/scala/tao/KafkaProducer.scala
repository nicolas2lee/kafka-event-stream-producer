package tao

import java.util.Properties

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Attributes, Materializer}
import net.ceedubs.ficus.Ficus._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.Future

object KafkaProducer {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val actorMaterializer: Materializer = ActorMaterializer()

    val config = actorSystem.settings.config.getConfig("akka.kafka.producer")

    val myTopic = config.getString("myTopic")
//    val SASL_JAAS_CONFIG = config.getString("sasl.jaas.config")
//    val SASL_MECHANISM = config.getString("sasl.mechanism")
//    val SECURITY_PROTOCOL = config.getString("security.protocol")
//    val SSL_PROTOCOL = config.getString("ssl.protocol")

    println(myTopic)
    val securityConfig = config.getConfig("security")
    import tao.Implicits._
    val producerSettings: ProducerSettings[String, String] =  ProducerSettings(actorSystem, new StringSerializer, new StringSerializer)
    //  .withProperties()
      .withProperties(securityConfig.as[Properties].toMap)
    val done: Future[Done] =
      Source(1 to 100)
        .map(_.toString)
        .map(id=> String.valueOf(id))
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
