package com.example.day.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class Inquiry(
                          firstName: String,
                          lastName: String,
                          email: String,
                          phone: String,
                          postcode: String
                        )

case object Inquiry {

  def toStringMap(i: Inquiry): Map[String,String] = Map(
    "firstName" ->  i.firstName,
    "lastName" -> i.lastName,
    "email" -> i.email,
    "phone" -> i.phone,
    "postcode" -> i.postcode
  )

  val inquiryDecoder: Decoder[Inquiry] = deriveDecoder
  val inquiryEncoder: Encoder[Inquiry] = deriveEncoder

}