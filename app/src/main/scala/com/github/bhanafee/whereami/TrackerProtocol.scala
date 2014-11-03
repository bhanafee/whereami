package com.github.bhanafee.whereami

object TrackerProtocol {
  import spray.json._
  import DefaultJsonProtocol._
  import org.joda.time.DateTime
  import org.joda.time.format.{ DateTimeFormatter, ISODateTimeFormat }

  // Primitives

  type Timestamp = DateTime
  type Degrees = Double

  type Tags = Seq[String]
  type DeviceId = String

  case class Point(latitude: Degrees, longitude: Degrees) {
    def close(other: Point) = (latitude - other.latitude).abs + (longitude - other.longitude).abs < 0.05
  }
  case class Position(location: Point, time: Timestamp)

  // Messages
  case class Tag(location: Point)
  case class Tagged(location: Point, tags: Tags)

  case class Report(id: DeviceId, position: Position)
  case class Entry(id: DeviceId, time: Timestamp, location: Option[Point], tags: Tags)

  // JSON

  implicit object TimestampFormat extends JsonFormat[Timestamp] {
    val format = ISODateTimeFormat.dateTime()
    override def read(json: JsValue) = format.parseDateTime(json.convertTo[String])
    override def write(time: Timestamp) = format.print(time).toJson
  }

  object Point extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Point.apply)
  }

  object Position extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Position.apply)
  }

  object Tagged extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Tagged.apply)
  }

}