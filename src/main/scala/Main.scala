package com.example.day

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.example.day.InquireService.inquireApp
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {

  val inquireService: InquireService = InquireService(_ => IO.pure(true))

  val httpServer: IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(inquireApp(inquireService))
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

  def run(args: List[String]): IO[ExitCode] = httpServer

}
