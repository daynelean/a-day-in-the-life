package com.example.day

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.scalatest._

class ServiceSpec extends FlatSpec {
  import ServiceSpec._

  "Service.inquireService " should " 404 " in {
    assert(runService(getRoot).status == notFound.status)
  }

  "Service.inquireService " should " 202 " in {
    assert(postInquiry(goodJson).status == accepted.status)
  }

  "Service.inquireService " should " 400 " in {
    assert(postInquiry(badJson).status == badRequest.status)
  }
}

case object ServiceSpec {

  val notFound: Response[IO] = Response.notFound
  val accepted: Response[IO] = Accepted().unsafeRunSync
  val badRequest: Response[IO] = BadRequest().unsafeRunSync

  val getRoot: Request[IO] = Request[IO](Method.GET, Uri(path = "/"))

  val badJson: String = """{ "bad": "json" }"""
  val goodJson: String =
    """{
         "firstName": "abc",
         "lastName": "abc",
         "email": "abc",
         "phone": "abc",
         "postcode": "abc"
       }"""

  def postInquiry(body: String): Response[IO] =
    runService(Request[IO](Method.POST, Uri(path = "/inquire")).withEntity(body))

  def runService(req: Request[IO]): Response[IO] =
    Service.inquireService.orNotFound(req).unsafeRunSync

}
