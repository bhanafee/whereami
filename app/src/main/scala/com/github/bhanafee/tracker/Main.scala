package com.tracker

import scala.concurrent.duration._

import akka.actor.{ ActorSystem, Props }
import akka.camel.CamelExtension
import akka.io.IO

import spray.can.Http

import com.typesafe.config.ConfigFactory

import com.tracker.gis._

object Main extends App {
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")
  val apiTimeout = config.getInt("http.timeout").seconds

  val connect = config.getString("gis.connect")

  implicit val system = ActorSystem("geotracker")

  val gis = system.actorOf(Props(classOf[PostGIS], connect), "gis")
  val recorder = system.actorOf(Props[Recorder], "recorder")
  val tracker = system.actorOf(Props(classOf[Tracker], recorder, gis, createFencer), "tracker")
  val api = system.actorOf(Props(classOf[RestInterface], gis, tracker, apiTimeout), "httpInterface")
  IO(Http) ! Http.Bind(listener = api, interface = host, port = port)
  CamelExtension(system).context.addRoutes(new RecordingRoutes)


  def createFencer = new Fencer {
    import org.joda.time.ReadablePeriod
    import TrackerProtocol.Meters

    val lifetime: ReadablePeriod = org.joda.time.Hours.hours(config.getInt("tracker.fence.lifetime"))
    val minRadius: Meters = config.getInt("tracker.fence.radius.min")
    val maxRadius: Meters = config.getInt("tracker.fence.radius.max")
    val defaultRadius: Meters = config.getInt("tracker.fence.radius.default")
  }
}