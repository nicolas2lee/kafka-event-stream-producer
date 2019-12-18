package tao

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

object WebServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("akka-http-kafka-producer")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val kafkaProducer = system.actorOf(Props[KafkaProducerActor], "kafkaProducerActor")

    val route =
      path("send") {
        post {
          // decompress gzipped or deflated requests if required
          decodeRequest {
            // unmarshal with in-scope unmarshaller
            entity(as[String]) { order =>
              complete {
                // ... write order to DB
                println(order)
                kafkaProducer ! order
                "message sent"
              }
            }
          }
        }
      }
    Http().bindAndHandle(route, "0.0.0.0", 9080)
    println("app running on 9080")
  }
}