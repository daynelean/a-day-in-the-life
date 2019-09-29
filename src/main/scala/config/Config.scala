package com.example.day.config

import cats.Applicative
import cats.effect.Sync
import cats.implicits._

final case class SqsConfig(endpoint: String,
                           region: String,
                           accessKey: String,
                           secretKey: String,
                           queueUrl: String
                          )

case object SqsConfig {

  def loadConfig[F[_]: Sync](): F[SqsConfig] = {
    // Might as well write a curried constructor, it's just as verbose.
    val curriedCon =
      (new SqsConfig(_: String, _:String, _:String, _:String, _:String)).curried

    Applicative[F].pure(curriedCon) <*>
      readEnvVar("SQS_ENDPOINT") <*>
      readEnvVar("SQS_REGION") <*>
      readEnvVar("SQS_ACCESS_KEY") <*>
      readEnvVar("SQS_SECRET_KEY") <*>
      readEnvVar("SQS_QUEUE_URL")
  }

  def readEnvVar[F[_]: Sync](name: String): F[String] = Sync[F].delay({
    sys.env(name)
  })
}

