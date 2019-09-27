package com.example.day

import cats.effect.IO
import com.example.day.InquireService.inquireApp
import org.http4s._
import org.scalatest._

class ServiceSpec extends FlatSpec {
  import ServiceSpec._

  val rootPath: Uri.Path = "/"
  val inquirePath: Uri.Path = "/inquire"


  "Service.inquireService " should " 404 " in {
    assert(
      get(successService, rootPath) == org.http4s.Status.NotFound
    )
    assert(
      post(successService, rootPath, goodJson) == org.http4s.Status.NotFound
    )
    assert(
      get(successService, inquirePath) == org.http4s.Status.NotFound
    )
    assert(
      get(failureService, inquirePath) == org.http4s.Status.NotFound
    )
  }

  "Service.inquireService " should " 202 " in {
    assert(
      post(successService, inquirePath, goodJson) == org.http4s.Status.Accepted
    )
  }

  "Service.inquireService " should " 400 " in {
    assert(
      post(successService, inquirePath, badJson) == org.http4s.Status.BadRequest
    )

    assert(
      post(failureService, inquirePath, badJson) == org.http4s.Status.BadRequest
    )
  }

  "Service.inquireService " should " 500 " in {
    assert(
      post(failureService, inquirePath, goodJson) == org.http4s.Status.InternalServerError
    )
  }
}

case object ServiceSpec {

  val badJson: String = """{ "bad": "json" }"""
  val goodJson: String =
    """{
         "firstName": "abc",
         "lastName": "abc",
         "email": "abc",
         "phone": "abc",
         "postcode": "abc"
       }"""

  val successService: InquireService = InquireService(_ => IO.pure(true))
  val failureService: InquireService = InquireService(_ => IO.pure(false))

  def get(service: InquireService, path: Uri.Path): org.http4s.Status =
    inquireApp(service)(Request[IO](Method.GET, Uri(path = path))).unsafeRunSync.status

  def post(service: InquireService, path: Uri.Path, body: String): org.http4s.Status =
    inquireApp(service)(Request[IO](Method.POST, Uri(path = path)).withEntity(body)).unsafeRunSync.status

}
