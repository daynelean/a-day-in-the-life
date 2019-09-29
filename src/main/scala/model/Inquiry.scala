package com.example.day.model

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

}