package com.trailmagic.jumper.web.model

import com.cyrusinnovation.inquisition.response.Response
import reflect.BeanProperty
import org.hibernate.validator.constraints.NotEmpty
import org.hibernate.validator.constraints.NotEmpty._

class ResponseFormData {
  def this(response: Response) = {
    this()
    title = response.title
    body = response.body
    id = response.id.getOrElse("")
  }

  @BeanProperty var title = ""

  @NotEmpty(message = "Question body cannot be empty")
  @BeanProperty var body = ""
  @BeanProperty var id = ""

  def toResponse: Response = {
      val responseId = id match {
            case (x) if !x.isEmpty => Some(x)
            case _ => None
        }
    Response(responseId, title, "tester", body)
  }
}