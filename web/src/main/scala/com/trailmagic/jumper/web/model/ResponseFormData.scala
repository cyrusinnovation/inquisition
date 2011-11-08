package com.trailmagic.jumper.web.model

import com.cyrusinnovation.inquisition.response.Response
import reflect.BeanProperty

class ResponseFormData {
  @BeanProperty var title = ""
  @BeanProperty var body = ""
  @BeanProperty var questionId = ""

  def toResponse: Response = {
    Response(None, title, "tester", body)
  }
}