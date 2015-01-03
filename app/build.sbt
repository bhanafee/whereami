import NativePackagerKeys._

name := "whereami"

packageArchetype.java_application

version := "0.1-SNAPSHOT"

organization := "com.github.bhanafee.whereami"

scalaVersion := "2.11.4"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")

libraryDependencies ++= {
  val akkaVersion       = "2.3.8"
  val sprayVersion      = "1.3.2"
  Seq(
    "com.typesafe.akka"  %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka"  %% "akka-camel"      % akkaVersion,
    "com.typesafe.akka"  %% "akka-slf4j"      % akkaVersion,
    "com.typesafe.slick" %% "slick"           % "2.1.0",
    "io.spray"           %% "spray-can"       % sprayVersion,
    "io.spray"           %% "spray-routing"   % sprayVersion,
    "io.spray"           %% "spray-json"      % "1.3.1",
    "joda-time"          %  "joda-time"       % "2.6",
    "org.postgresql"     %  "postgresql"      % "9.3-1102-jdbc4",
    "ch.qos.logback"     %  "logback-classic" % "1.1.2",
    "com.typesafe.akka"  %% "akka-testkit"    % akkaVersion   % "test",
    "org.scalatest"      %% "scalatest"       % "2.2.1"       % "test"
  )
}

// Assembly settings
mainClass in Global := Some("com.github.bhanafee.whereami.Main")

// Docker settings
dockerBaseImage in Docker := "dockerfile/java:oracle-java8"

dockerRepository := Some("bhanafee")

maintainer in Docker := "Brian Hanafee <bhanafee@gmail.com>"

dockerExposedPorts in Docker := Seq(80)
