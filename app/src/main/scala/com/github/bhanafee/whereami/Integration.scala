package com.github.bhanafee.whereami

import akka.actor.{ Actor, ActorLogging, Props }
import akka.camel.{ CamelMessage, Oneway, Producer }

import org.apache.camel.{ Exchange, Processor }
import org.apache.camel.builder.RouteBuilder

class Recorder(recording: String) extends Actor with ActorLogging {
  val endpoint = context.actorOf(Endpoint.props(recording), "recorderEndpoint")

  import TrackerProtocol.{ Entry, Point }

  def receive = {
    case Entry(id, time, location, tags) =>
      val body = location match {
        case Some(Point(lon, lat)) =>
          f"""$id, $time, $lon%3.5f, $lat%3.5f, ${tags.mkString(", ")}"""
        case None =>
          f"""$id, $time, , , ${tags.mkString(", ")}"""
      }
      endpoint ! CamelMessage(body, Map.empty)
  }

}

class Endpoint(val endpointUri: String) extends Actor with Producer
object Endpoint {
  def props(endpointUri: String): Props = Props(new Endpoint(endpointUri))
}

class RecordingRoutes(recording: String) extends RouteBuilder {
  def configure {
    from(recording).process(new Processor() {
      def process(exchange: Exchange) {
        // TODO: send this somewhere real
        println(exchange.getIn.getBody)
      }
    })
  }
}