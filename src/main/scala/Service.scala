package com.example.day

import cats.data.EitherT
import cats.effect.{IO, Sync}
import cats.implicits._
import com.example.day.model.Inquiry
import com.example.day.model.Inquiry.inquiryDecoder
import org.http4s.circe.jsonOf
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.{EntityDecoder, HttpApp, HttpRoutes, Response}

final case class InquireService(saveAction: Inquiry => EitherT[IO, Throwable, Unit])

case object InquireService {

  def log(s: String): IO[Unit] = IO.apply(println(s))

  def apply(saveAction: Inquiry => EitherT[IO, Throwable, Unit]): InquireService = new InquireService(saveAction)

  private implicit val decoder: EntityDecoder[IO, Inquiry] = jsonOf[IO, Inquiry](Sync[IO],inquiryDecoder)

  private def handlePost(service: InquireService)(inquiry: Inquiry): IO[Response[IO]] =
    service.saveAction(inquiry).foldF(e => log(e.getMessage()) *> InternalServerError(), _ => Accepted())

  def inquireApp(service: InquireService): HttpApp[IO] =
    HttpRoutes.of[IO] {
      case req@POST -> Root / "inquire" =>
        req.attemptAs[Inquiry].foldF(_ => BadRequest(), handlePost(service))
    }.orNotFound

}
