import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val http4sVersion = "0.20.11"
val circeVersion = "0.11.1"

lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin, AssemblyPlugin)
  .settings(
    name := "a-day-in-the-life",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      // comment this out so libraries default to no op logging
      //"org.slf4j" % "slf4j-simple" % "1.7.28", // for aws sdk
      "com.amazonaws" % "aws-java-sdk-sqs" % "1.11.640",
      "io.circe" %% "circe-literal" % circeVersion % Test,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.typelevel" %% "cats-core" % "2.0.0",
      "org.typelevel" %% "cats-effect" % "2.0.0",
      "org.scalatra.scalate" %% "scalate-core" % "1.9.4"
    ),
    dockerfile in docker := {
      val artifact: File = assembly.value
      val artifactTargetPath = s"/app/${artifact.name}"
      new Dockerfile {
        from("openjdk:13-alpine")
        add(artifact, artifactTargetPath)
        entryPoint("java", "-cp", artifactTargetPath)
      }
    },
    imageNames in docker := Seq(
      // Sets the latest tag
      ImageName(s"${organization.value}/${name.value}:latest"),

      // Sets a name with a tag that contains the project version
      ImageName(
        repository = name.value,
        tag = git.gitHeadCommit.value
      )
    )
  )

scalacOptions ++= Seq("-Ypartial-unification")

Compile /run / fork := true
Compile /run / javaOptions += "-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"
