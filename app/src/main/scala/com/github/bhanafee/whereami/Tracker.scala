package com.github.bhanafee.whereami

import akka.actor.{ Actor, ActorLogging, ActorRef, PoisonPill, Props, ReceiveTimeout }
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{ Failure, Success }
import TrackerProtocol._

class Tracker(val recorder: ActorRef, val gis: ActorRef, val fencer: Fencer) extends Actor with ActorLogging {

  import context.dispatcher
  import akka.pattern.ask

  val Nowhere: Tags = Seq("Nowhere")

  // TODO: parameterize timeout, different for Tag and FindNearest.
  // FindNearest is based on (same as?) API timeout. Tag can be longer.
  implicit val timeout = Timeout(10 seconds)

  def receive = {
    case Checkin(device, time) => recorder ! Entry(device, time, None, Nil)

    case Report(device, position) => {
      val responder = context.sender
      gis ? FindNearest(position.location) andThen {
        case Success(nearest: Nearest) =>
         if (nearest.location.close(position.location))
           responder ! fencer.bound(position.time, nearest.location, Some(nearest.distance))
          else log.error("Nearest position did not match report")
        case _ =>
          log.warning("Failed to compute nearest boundary to reported position")
          responder ! fencer.bound(position.time, position.location, None)
      }

      gis ? Tag(position.location) andThen {
        case Success(Tagged(loc, tags)) =>
          if (loc.close(position.location))
            recorder ! Entry(device, position.time, Some(position.location), if (tags.isEmpty) Nowhere else tags)
          else log.error("Tagged position did not match report")
        case _ =>
          log.warning("Failed to tag reported position")
          recorder ! Entry(device, position.time, Some(position.location), Seq.empty)
      }
    }
  }
}

import org.joda.time.ReadablePeriod
trait Fencer {
  val lifetime: ReadablePeriod
  val minRadius: Meters
  val maxRadius: Meters
  val defaultRadius: Meters

  def bound(time: Timestamp, location: Point, nearest: Option[Meters]): Boundary = {
    val radius = nearest.getOrElse(defaultRadius).max(minRadius).min(maxRadius)
    val until = time.plus(lifetime)
    Boundary(Circle(location, radius), until)
  }
}