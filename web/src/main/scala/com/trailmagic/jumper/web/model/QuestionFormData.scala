package com.trailmagic.jumper.web.model

import reflect.BeanProperty
import com.cyrusinnovation.inquisition.questions.Question
import org.hibernate.validator.constraints.NotEmpty

class QuestionFormData {

  @NotEmpty(message = "Question title cannot be empty")
  @BeanProperty var title = ""
  @NotEmpty(message = "Question body cannot be empty")
  @BeanProperty var body = ""

  @BeanProperty var tags = ""
  @BeanProperty var id = ""
  @BeanProperty var client = ""

    def this(question: Question) = {
        this()
        this.title = question.title
        this.body = question.body
        this.tags = question.tags mkString ","
        this.id = question.id.getOrElse("")
        this.client = question.client
    }

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
      Question(questionId, title, "tester", body, tagList, client = client)
  }

}