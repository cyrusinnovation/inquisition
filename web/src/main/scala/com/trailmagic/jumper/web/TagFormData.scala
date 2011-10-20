package com.trailmagic.jumper.web

import reflect.BeanProperty
import com.cyrusinnovation.inquisition.questions.Question

class TagFormData {
  @BeanProperty var tagQuery = ""

  def toTagList: List[String] = {
    tagQuery.split(" ").toList
  }
}