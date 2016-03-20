name := "WikiGrowth"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= (
  "org.scalatest" %% "scalatest" % "2.2.6" ::
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.2" ::
  Nil
) map (_ % Test)
