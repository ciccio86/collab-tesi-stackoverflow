name := """stackexchange-metrics-calculator"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.github.tototoshi" %% "scala-csv" % "1.2.2",
  "com.github.nscala-time" %% "nscala-time" % "2.2.0",
  "org.jsoup" % "jsoup" % "1.8.3"
)

