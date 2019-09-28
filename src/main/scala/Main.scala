package com.example.day

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits._
import com.example.day.InquireService.inquireApp
import com.example.day.Queue.{createQueue, sendMessage, sqsClient}
import com.example.day.model.Inquiry
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder;



object Main extends IOApp {

  def mkApp(queueMessage: String => IO[Unit]): HttpApp[IO] = {
    def saveAction(inquiry: Inquiry): EitherT[IO, Throwable, Unit] =
      queueMessage(inquiry.asJson.noSpaces).attemptT

    inquireApp(InquireService(saveAction))
  }

  def mkHttpServer(app: HttpApp[IO]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(app)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

  private implicit val F = Sync[IO]

  def run(args: List[String]): IO[ExitCode] = {
    val queueName: String = "queue"
    for {
      sqs <- sqsClient()
      qUrl <- createQueue(sqs, queueName)
      httpServer <- mkHttpServer(mkApp(sendMessage[IO](sqs, qUrl)))
    } yield (httpServer)
  }


}
