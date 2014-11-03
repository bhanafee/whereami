package com.github.bhanafee.whereami

import akka.actor.{ Actor, ActorLogging, ActorRef, PoisonPill, Props, ReceiveTimeout }
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{ Failure, Success }
import TrackerProtocol._

class Tracker(val recorder: ActorRef, val gis: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher
  import akka.pattern.ask

  val Nowhere: Tags = Seq("Nowhere")

  implicit val timeout = Timeout(10 seconds)

  def receive = {
    case Report(device, position) => {
      val responder = context.sender
      gis ? Tag(position.location) andThen {
        case Success(Tagged(loc, tags)) =>
          responder ! Tagged(loc, tags)
          recorder ! Entry(device, position.time, Some(position.location), if (tags.isEmpty) Nowhere else tags)
        case _ => log.warning("Failed to tag reported position")
      }
    }
  }
}
