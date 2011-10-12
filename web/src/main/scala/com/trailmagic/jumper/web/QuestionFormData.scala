package com.trailmagic.jumper.web

import reflect.BeanProperty
import com.cyrusinnovation.inquisition.questions.{Question, QuestionAnswer}

class QuestionFormData {
  @BeanProperty var title = ""
  @BeanProperty var body = ""
  @BeanProperty var tags = ""

  def toQuestion: Question = {
    Question(None, title, "tester", body, tags.split(",").toList)
  }
}