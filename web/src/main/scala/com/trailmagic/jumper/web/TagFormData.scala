package com.trailmagic.jumper.web

import reflect.BeanProperty
import com.cyrusinnovation.inquisition.questions.Question

class TagFormData {
  @BeanProperty var tagQuery = ""

  def toTagList: List[String] = {
    tagQuery.split(" ").toList
  }
}

//class QuestionAnswerFormData {
//  @BeanProperty var title = ""
//  @BeanProperty var body = ""
//  @BeanProperty var questionId = ""
//  def toQuestionAnswer: QuestionAnswer = {
//    QuestionAnswer(title, "tester", body)
//  }
//}