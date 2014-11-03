package com.github.bhanafee.whereami.gis

import com.github.bhanafee.whereami.TrackerProtocol.{ Point, Tags }

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.StaticQuery.interpolation

class PostGIS(connect: String) extends GIS {
  val db = Database.forURL(connect, driver = "org.postgresql.Driver")

  def tags(location: Point): Tags = {
  	db.withSession { implicit session =>
      val lon = location.longitude
      val lat = location.latitude
      val tagsQuery = sql"""
        SELECT name
        FROM areas.monitored
        WHERE
        ST_Intersects(ST_SetSRID(ST_MakePoint($lon, $lat),4326), monitored.geom)
        """.as[(String)]
      tagsQuery.list
    }
  }
}