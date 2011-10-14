package com.trailmagic.jumper.web

import reflect.BeanProperty
import com.cyrusinnovation.inquisition.questions.{Question, QuestionAnswer}
import com.mongodb.MongoException.DuplicateKey

class QuestionFormData {
  @BeanProperty var title = ""
  @BeanProperty var body = ""
  @BeanProperty var tags = ""

  def toQuestion: Question = {
    var tagList: List[String]          = List()
    try {
    tagList = tags
                      .split(",")
                      .map(x=> x.trim)
                      .filterNot(x => x.isEmpty)
                      .toList
    } catch {
            case e: NullPointerException => tagList
    }
    Question(None, title, "tester", body, tagList)
  }
}