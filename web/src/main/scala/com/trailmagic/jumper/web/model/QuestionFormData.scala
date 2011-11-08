package com.trailmagic.jumper.web.model

import reflect.BeanProperty
import com.cyrusinnovation.inquisition.questions.Question

class QuestionFormData {
  @BeanProperty var title = ""
  @BeanProperty var body = ""
  @BeanProperty var tags = ""

  def toQuestion: Question = {
    val tagList = Option(tags)
                  .getOrElse("")
                  .split(",")
                  .map(x=> x.trim)
                  .filterNot(x => x.isEmpty)
                  .toList
    Question(None, title, "tester", body, tagList)
  }
}