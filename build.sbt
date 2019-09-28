import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val http4sVersion = "0.20.11"
val circeVersion = "0.11.1"

lazy val root = (project in file("."))
  .settings(
    name := "a-day-in-the-life",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "org.slf4j" % "slf4j-simple" % "1.7.28", // for aws sdk
      "com.amazonaws" % "aws-java-sdk-sqs" % "1.11.640",
      "io.circe" %% "circe-literal" % circeVersion % Test,
      "io.circe" %% "circe-generic" % circeVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.typelevel" %% "cats-core" % "2.0.0",
      "org.typelevel" %% "cats-effect" % "2.0.0"
    )
  )

scalacOptions ++= Seq("-Ypartial-unification")

Compile /run / fork := true
Compile /run / javaOptions += "-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"
