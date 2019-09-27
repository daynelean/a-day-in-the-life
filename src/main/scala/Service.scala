package com.example.day

import cats.effect.IO
import com.example.day.model.Inquiry
import io.circe.generic.auto._
import org.http4s.circe.jsonOf
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.{EntityDecoder, HttpApp, HttpRoutes, Response}

final case class InquireService(saveAction: Inquiry => IO[Boolean])

case object InquireService {

  def apply(saveAction: Inquiry => IO[Boolean]): InquireService = new InquireService(saveAction)

  private implicit val decoder: EntityDecoder[IO, Inquiry] = jsonOf[IO, Inquiry]

  private def handlePost(service: InquireService)(inquiry: Inquiry): IO[Response[IO]] =
    service.saveAction(inquiry).flatMap(if (_) Accepted() else InternalServerError())

  def inquireApp(service: InquireService): HttpApp[IO] =
    HttpRoutes.of[IO] {
      case req@POST -> Root / "inquire" =>
        req.attemptAs[Inquiry].foldF(_ => BadRequest(), handlePost(service))
    }.orNotFound

}
