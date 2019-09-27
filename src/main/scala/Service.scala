package com.example.day

import cats.effect.IO
import com.example.day.model.Inquiry
import io.circe.generic.auto._
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe.jsonOf
import org.http4s.dsl.io._

case object Service {

  implicit val decoder: EntityDecoder[IO, Inquiry] = jsonOf[IO, Inquiry]

  val inquireService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "inquire" =>
      req.as[Inquiry].flatMap(_ => Accepted()).handleErrorWith(_ => BadRequest())
  }

}
