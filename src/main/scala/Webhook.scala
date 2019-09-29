package com.example.day

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits._
import com.example.day.app.InquireService
import com.example.day.app.InquireService.inquireApp
import com.example.day.config.SqsConfig.loadConfig
import com.example.day.data.SQS.{sendMessage, sqsClient}
import com.example.day.model.Inquiry
import com.example.day.model.Inquiry.inquiryEncoder
import io.circe.syntax._
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder;

object Webhook extends IOApp {

  def mkApp(queueMessage: String => IO[Unit]): HttpApp[IO] = {
    def saveAction(inquiry: Inquiry): EitherT[IO, Throwable, Unit] =
      queueMessage(inquiry.asJson(inquiryEncoder).noSpaces).attemptT

    inquireApp(InquireService(saveAction))
  }

  def mkHttpServer(app: HttpApp[IO]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(app)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

  private implicit val F = Sync[IO]

  def run(args: List[String]): IO[ExitCode] = {
    for {
      conf <- loadConfig()
      sqs <- sqsClient(conf)
      httpServer <- mkHttpServer(mkApp(sendMessage[IO](sqs, conf.queueUrl)))
    } yield (httpServer)
  }

}
