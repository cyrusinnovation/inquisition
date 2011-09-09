package com.trailmagic.jumper.web

import org.springframework.web.servlet.ModelAndView
import util.SecurityHelper
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired
import com.trailmagic.jumper.core.TimeSource
import scala.collection.JavaConverters._
import com.cyrusinnovation.inquisition.questions.QuestionRepository

@Controller
class HomePageController @Autowired()(questionRepository: QuestionRepository, timeSource: TimeSource) {
  @RequestMapping(Array("/"))
  def showIndex() = {
    val model = Map("currentUser" -> SecurityHelper.getAuthenticatedUser,
      "questions" -> questionRepository.findRecent(timeSource.now))
    new ModelAndView("index", model.asJava)
  }
}