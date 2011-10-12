package com.trailmagic.jumper.web

import com.cyrusinnovation.inquisition.questions.QuestionAnswer
import reflect.BeanProperty

class QuestionAnswerFormData {
  @BeanProperty var title = ""
  @BeanProperty var body = ""
  @BeanProperty var questionId = ""

  def toQuestionAnswer: QuestionAnswer = {
    QuestionAnswer(title, "tester", body)
  }
}