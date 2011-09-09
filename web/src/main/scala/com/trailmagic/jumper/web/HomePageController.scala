package com.trailmagic.jumper.web

import util.SecurityHelper
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import com.trailmagic.jumper.core.TimeSource
import scala.collection.JavaConverters._
import com.cyrusinnovation.inquisition.questions.QuestionRepository
import org.springframework.web.servlet.tags.Param
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.{ModelAttribute, RequestParam, RequestMethod, RequestMapping}

@Controller
class HomePageController @Autowired()(questionRepository: QuestionRepository, timeSource: TimeSource) {
  @RequestMapping(Array("/"))
  def showIndex() = {
    val model = Map("currentUser" -> SecurityHelper.getAuthenticatedUser,
      "questions" -> questionRepository.findRecent(timeSource.now))
    new ModelAndView("index", model.asJava)
  }

  @RequestMapping(Array("/questions/new"))
  def showNewQuestionForm(): String = {
    "new-question"
  }

  @RequestMapping(value = Array("/questions"), method = Array(RequestMethod.POST))
  def addQuestion(@ModelAttribute question: QuestionFormData) = {
    questionRepository.save(question.toQuestion)
    "redirect:/"
  }
}