import AssemblyKeys._
import com.typesafe.sbt.SbtStartScript

name := "tracker"

version := "0.1-SNAPSHOT"

organization := "com.github.bhanafee.whereami"

scalaVersion := "2.11.2"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                  "Sonatype snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots/",
                  "Spray Repository"    at "http://repo.spray.io",
                  "Spray Nightlies"     at "http://nightlies.spray.io/")

libraryDependencies ++= {
  val akkaVersion       = "2.3.6"
  val sprayVersion      = "1.3.2"
  Seq(
    "com.typesafe.akka"  %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka"  %% "akka-camel"      % akkaVersion,
    "com.typesafe.akka"  %% "akka-slf4j"      % akkaVersion,
    "com.typesafe.slick" %% "slick"           % "2.1.0",
    "io.spray"           %% "spray-can"       % sprayVersion,
    "io.spray"           %% "spray-routing"   % sprayVersion,
    "io.spray"           %% "spray-json"      % "1.3.0",
    "joda-time"          %  "joda-time"       % "2.5",
    "org.postgresql"     %  "postgresql"      % "9.3-1102-jdbc4",
    "ch.qos.logback"     %  "logback-classic" % "1.1.2",
    "com.typesafe.akka"  %% "akka-testkit"    % akkaVersion   % "test",
    "org.scalatest"      %% "scalatest"       % "2.2.1"       % "test"
  )
}

// Assembly settings
mainClass in Global := Some("com.github.bhanafee.whereami.Main")

jarName in assembly := "tracker-server.jar"

assemblySettings

// StartScript settings
seq(SbtStartScript.startScriptForClassesSettings: _*)
