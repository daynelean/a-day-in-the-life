package com.example.day

import cats.effect.Sync

case object SES {
  type Email = String
  type Body = String

  def sendMail[F[_]: Sync](email: Email, body: Body): F[Unit] = Sync[F].delay({
    val sleepInMillis: Long = 500
    Thread.sleep(sleepInMillis)
     println(body)
  })
}