package com.example.day

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.implicits._
import com.example.day.Service.inquireService
import org.http4s.server.blaze.BlazeServerBuilder


object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(inquireService.orNotFound)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
