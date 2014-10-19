package com.github.bhanafee.whereami.gis

import com.github.bhanafee.whereami.TrackerProtocol.{ Point, Meters, Tags }
class Stub extends GIS {
  def tags(location: Point): Tags = Nil
  def nearest(location: Point): Meters = 0
}