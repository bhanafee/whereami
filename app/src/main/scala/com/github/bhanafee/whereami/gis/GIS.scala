package com.github.bhanafee.whereami.gis

import akka.actor.Actor

import com.github.bhanafee.whereami.TrackerProtocol

trait GIS extends Actor {

  import TrackerProtocol._

  def tags(location: Point): Tags
  def nearest(location: Point): Meters

  def receive = {
    case Tag(location) => sender ! Tagged(location, tags(location))
    case FindNearest(location) => sender ! Nearest(location, nearest(location))
  }
}