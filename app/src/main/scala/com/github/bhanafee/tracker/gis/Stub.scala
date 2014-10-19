package com.tracker.gis

import com.tracker.TrackerProtocol.{ Point, Meters, Tags }
class Stub extends GIS {
  def tags(location: Point): Tags = Nil
  def nearest(location: Point): Meters = 0
}