package com.github.bhanafee.whereami

import akka.actor.{ Actor, ActorLogging, Props }
import akka.camel.{ CamelMessage, Oneway, Producer }

import org.apache.camel.{ Exchange, Processor }
import org.apache.camel.builder.RouteBuilder

class Recorder extends Actor with ActorLogging {
  val endpoint = context.actorOf(Props(classOf[RecorderEndpoint]), "recorderEndpoint")

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

class RecorderEndpoint extends Actor with Producer {
  def endpointUri = "direct:record"
}

class RecordingRoutes extends RouteBuilder {
  def configure {
    from("direct:record").process(new Processor() {
      def process(exchange: Exchange) {
        // TODO: send this somewhere real
        println(exchange.getIn.getBody)
      }
    })
  }
}