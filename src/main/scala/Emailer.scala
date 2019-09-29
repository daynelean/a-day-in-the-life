package com.example.day

import cats.effect.{ExitCode, IO, IOApp}
import com.example.day.model.Inquiry

object Emailer extends IOApp {

  private def postcodeIsLocal(postcode: String): Boolean = {
    val localPattern: String = "^NW8 *[0-9A-Z]{3}?$"
    postcode.matches(localPattern)
  }

  private def generateResponse(inquiry: Inquiry): IO[String] = {
    if (postcodeIsLocal(inquiry.postcode)) Templates.yesTemplate(inquiry)
    else Templates.noTemplate(inquiry)
  }


  def run(args: List[String]): IO[ExitCode] = {
    IO.pure(ExitCode.Success)
  }
}
