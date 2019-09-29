package com.example.day

import cats.effect.Sync
import com.example.day.model.Inquiry
import com.example.day.model.Inquiry.toStringMap
import org.fusesource.scalate.TemplateEngine

case object Templates {

  private val yesTemplate: String = "yes.mustache"
  private val noTemplate: String = "no.mustache"

  private def render[F[_]: Sync](
                                  context: Map[String, String],
                                  templateName: String
                                ): F[String] = {
    Sync[F].delay({
      // TODO maybe shouldn't create a new TemplateEngine for each call
      val engine = new TemplateEngine
      engine.layout(templateName, context)
    })
  }

  def yesTemplate[F[_]: Sync](inquiry: Inquiry): F[String] =
    render(toStringMap(inquiry),yesTemplate)

  def noTemplate[F[_]: Sync](inquiry: Inquiry): F[String] =
    render(toStringMap(inquiry),noTemplate)

//  def render0(inquiry: Inquiry): String = {
//    val uri: String = "no.mustache"
//    val context: Map[String, String] = Map(
//      "firstName" -> inquiry.firstName,
//      "lastName" -> inquiry.lastName
//    )
//    val engine = new TemplateEngine
//    engine.layout(uri, context)
//  }

}
