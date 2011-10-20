package com.trailmagic.jumper.web

import com.cyrusinnovation.inquisition.response.Response
import com.cyrusinnovation.inquisition.questions.Question
import reflect.BeanProperty

class QuestionAnswerFormData {
  @BeanProperty var title = ""
  @BeanProperty var body = ""
  @BeanProperty var questionId = ""

  def toQuestionAnswer: Response = {
    Response(title, "tester", body)
  }
}