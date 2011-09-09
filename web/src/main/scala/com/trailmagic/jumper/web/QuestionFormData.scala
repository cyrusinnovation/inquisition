package com.trailmagic.jumper.web

import reflect.BeanProperty
import com.cyrusinnovation.inquisition.questions.Question


class QuestionFormData {
  @BeanProperty var title = ""

  def toQuestion: Question = {
    Question(None, title, "tester")
  }
}