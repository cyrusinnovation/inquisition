package com.trailmagic.jumper.web

import reflect.BeanProperty
import com.cyrusinnovation.inquisition.questions.{Question, QuestionAnswer}

class QuestionFormData {
  @BeanProperty var title = ""
  @BeanProperty var body = ""

  def toQuestion: Question = {
    Question(None, title, "tester", body, List())
  }
}

class QuestionAnswerFormData {
  @BeanProperty var title = ""
  @BeanProperty var body = ""
  @BeanProperty var questionId = ""
  def toQuestionAnswer: QuestionAnswer = {
    QuestionAnswer(title, "tester", body)
  }
}