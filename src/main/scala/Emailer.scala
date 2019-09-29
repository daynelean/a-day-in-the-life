package com.example.day

import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits._
import com.example.day.data.SQS.{Message, deleteMessage, fetchMessages, sqsClient}
import com.example.day.data.SES.sendMail
import com.example.day.model.Inquiry
import com.example.day.model.Inquiry.inquiryDecoder
import com.example.day.templates.Templates
import io.circe.parser.decode

import scala.concurrent.duration.{FiniteDuration, TimeUnit}

object Emailer extends IOApp {

  private def postcodeIsLocal(postcode: String): Boolean = {
    val localPattern: String = "^NW8 *[0-9A-Z]{3}?$"
    postcode.matches(localPattern)
  }

  private def generateResponse[F[_]: Sync](inquiry: Inquiry): F[String] = {
    if (postcodeIsLocal(inquiry.postcode)) Templates.yesTemplate(inquiry)
    else Templates.noTemplate(inquiry)
  }

  private def logError[F[_]: Sync](t: Throwable): F[Unit] = Sync[F].delay({
    println("error" + t.getMessage())
  })

  private def processInquiry[F[_]: Sync](inquiry: Inquiry): F[Unit] =
    generateResponse(inquiry).flatMap(sendMail[F](inquiry.email))

  private def processMessage[F[_]: Sync](msg: Message): F[Unit] = {
    // couldn't find the cats version of haskell's
    // either :: (a -> c) -> (b -> c) -> Either a b -> c
    //
    // there's probably also a way to lift Either => Sync[F]
    // but I couldn't find it
    decode[Inquiry](msg.getBody())(inquiryDecoder) match {
      case Left(e: io.circe.Error) =>
        Sync[F].raiseError(e)

      case Right(i: Inquiry) =>
        processInquiry(i)
    }
  }

  private def fetchAndProcessMessages[F[_]: Sync](): F[Unit] = {
    val queueName: String = "queue" // TODO, need the queueUrl
    for {
      sqs <- sqsClient()
      msgs <- fetchMessages(sqs, queueName)
      _ <- msgs.traverse_(msg =>
              processMessage(msg) *>
              deleteMessage(sqs, queueName)(msg.getReceiptHandle())
      )
    } yield ()
  }

  // TODO not sure if this is going to blow the stack.
  private def go(sleepTime: FiniteDuration): IO[Unit] =
    fetchAndProcessMessages[IO]() *> IO.sleep(sleepTime) *> go(sleepTime)

  def run(args: List[String]): IO[ExitCode] = {
    val sleepLength: Long = 10
    val sleepUnit: TimeUnit = scala.concurrent.duration.SECONDS
    val sleepTime: FiniteDuration = new FiniteDuration(sleepLength, sleepUnit)
    go(sleepTime).as(ExitCode.Success)
  }
}
