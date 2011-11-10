package com.trailmagic.jumper.web.model

import com.cyrusinnovation.inquisition.response.Response
import reflect.BeanProperty

class ResponseFormData {
  def this(response: Response) = {
    this()
    title = response.title
    body = response.body
    id = response.id.getOrElse("")
  }

  @BeanProperty var title = ""
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