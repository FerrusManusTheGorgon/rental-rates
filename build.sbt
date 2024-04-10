ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "selenium-test"
  )
libraryDependencies ++= Seq(
  "org.seleniumhq.selenium" % "selenium-java" % "4.9.1",
  "io.getquill" %% "quill-async-postgres" % "3.12.0",
  "org.postgresql" % "postgresql" % "42.3.1",
  "org.flywaydb" % "flyway-core" % "9.20.0",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "org.jsoup" % "jsoup" % "1.14.3"


)



