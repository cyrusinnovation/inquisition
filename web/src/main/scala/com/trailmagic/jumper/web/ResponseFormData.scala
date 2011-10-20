package com.trailmagic.jumper.web

import com.cyrusinnovation.inquisition.response.Response
import com.cyrusinnovation.inquisition.questions.Question
import reflect.BeanProperty

class ResponseFormData {
  @BeanProperty var title = ""
  @BeanProperty var body = ""
  @BeanProperty var questionId = ""

  def toResponse: Response = {
    Response(None, title, "tester", body)
  }
}