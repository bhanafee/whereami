package com.github.bhanafee.whereami.gis

import com.github.bhanafee.whereami.TrackerProtocol.{ Point, Meters, Tags }

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

  def nearest(location: Point): Meters = {
  	db.withSession { implicit session =>
      val lon = location.longitude
      val lat = location.latitude
      val nearestQuery = sql"""
        WITH
          device AS (
            SELECT ST_SetSRID(ST_MakePoint($lon, $lat),4326) AS location
          ),
          rough AS (
            SELECT geom
            FROM areas.monitored, device
            WHERE NOT ST_Intersects(device.location, geom)
            ORDER BY geom <#> device.location
            LIMIT 10
          )
        SELECT
          ST_Distance(device.location, geom, false) AS distance
        FROM device, rough
        ORDER BY distance
        LIMIT 1
        """.as[(Meters)]
      nearestQuery.list.head
    }
  }
}