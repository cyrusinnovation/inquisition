package com.trailmagic.jumper.web.model

import reflect.BeanProperty

class TagFormData {
  @BeanProperty var tagQuery = ""

  def toTagList: List[String] = {
    tagQuery.split(" ").toList
  }
}