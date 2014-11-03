package com.github.bhanafee.whereami

import scala.concurrent.duration._

import akka.actor.{ ActorSystem, Props }
import akka.camel.CamelExtension
import akka.io.IO

import spray.can.Http

import com.typesafe.config.ConfigFactory

import com.github.bhanafee.whereami.gis._

object Main extends App {
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")
  val apiTimeout = config.getInt("http.timeout").seconds

  val connect = config.getString("gis.connect")

  val recording = config.getString("recording.endpoint")

  implicit val system = ActorSystem("geotracker")

  val gis = system.actorOf(Props(classOf[PostGIS], connect), "gis")
  val recorder = system.actorOf(Props(classOf[Recorder], recording), "recorder")
  val tracker = system.actorOf(Props(classOf[Tracker], recorder, gis), "tracker")
  val api = system.actorOf(Props(classOf[RestInterface], gis, tracker, apiTimeout), "httpInterface")
  IO(Http) ! Http.Bind(listener = api, interface = host, port = port)
  CamelExtension(system).context.addRoutes(new RecordingRoutes(recording))
}