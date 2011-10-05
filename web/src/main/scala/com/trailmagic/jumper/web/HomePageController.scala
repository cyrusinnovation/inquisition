package com.trailmagic.jumper.web

import util.SecurityHelper
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import com.trailmagic.jumper.core.TimeSource
import scala.collection.JavaConverters._
import com.cyrusinnovation.inquisition.questions.QuestionRepository
import org.springframework.web.bind.annotation._
import org.springframework.web.servlet.ModelAndView
import org.slf4j._

@Controller
class HomePageController @Autowired()(questionRepository: QuestionRepository, timeSource: TimeSource) {
  @RequestMapping(Array("/"))
  def showIndex() = {
    val model = Map("currentUser" -> SecurityHelper.getAuthenticatedUser,
    "questions" -> questionRepository.findRecent(timeSource.now),
    "tags" -> questionRepository.findUniqueTagNamesOrderedByTagName())
    new ModelAndView("index", model.asJava)
  }


}