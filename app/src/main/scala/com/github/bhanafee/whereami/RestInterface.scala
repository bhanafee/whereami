package com.github.bhanafee.whereami

import scala.concurrent.duration._

import akka.actor._

import spray.routing._
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

import TrackerProtocol._

class RestInterface(val gis: ActorRef, val tracker: ActorRef, val apiTimeout: Duration) extends HttpServiceActor with RestApi {
  def receive = runRoute(routes)
}

trait RestApi extends HttpService with ActorLogging { actor: Actor =>
  import context.dispatcher

  val gis: ActorRef
  val tracker: ActorRef

  val apiTimeout: Duration

  val responder: RequestContext=>ActorRef = rc => context.actorOf(Props(classOf[Responder], rc, apiTimeout))

  val getLatLon = get & parameters('latitude.as[Degrees], 'longitude.as[Degrees])

  def routes: Route =
    (path("tags") & getLatLon) { (lat, lon) => ctx =>
      gis.tell(Tag(Point(lat, lon)), responder(ctx))
    } ~
    (path("checkin" / PathElement) & entity(as[Point])) { (device: DeviceId, point: Point) => ctx =>
      tracker.tell(Report(device, Position(point, new Timestamp())), responder(ctx))
    }

}

class Responder(requestContext: RequestContext, apiTimeout: Duration) extends Actor with ActorLogging {
  context.setReceiveTimeout(apiTimeout)

  def receive = {
    case tags: Tagged =>
      requestContext.complete(StatusCodes.OK, tags)
      self ! PoisonPill

    case ReceiveTimeout =>
      log.warning("Timeout on responder")
      requestContext.complete(StatusCodes.ServiceUnavailable)
      self ! PoisonPill
  }
}