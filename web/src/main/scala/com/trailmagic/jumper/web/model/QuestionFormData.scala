package com.trailmagic.jumper.web.model

import reflect.BeanProperty
import com.cyrusinnovation.inquisition.questions.Question

class QuestionFormData {
  @BeanProperty var title = ""
  @BeanProperty var body = ""
  @BeanProperty var tags = ""
  @BeanProperty var id = ""

  def toQuestion: Question = {
    val tagList = Option(tags)
                  .getOrElse("")
                  .split(",")
                  .map(x=> x.trim)
                  .filterNot(x => x.isEmpty)
                  .toList
    val questionId = id match {
//      case null => None
      case (x) if !x.isEmpty  => Some(x)
      case _ => None
    }
    Question(questionId, title, "tester", body, tagList)
  }
}