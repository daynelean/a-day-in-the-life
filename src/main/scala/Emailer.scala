package com.example.day

import cats.effect.{ExitCode, IO, IOApp};

object Emailer extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    IO.pure(ExitCode.Success)
  }
}
