package com.trailmagic.jumper.web

import org.springframework.web.bind.annotation.{ModelAttribute, RequestMethod, PathVariable, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import com.cyrusinnovation.inquisition.questions.QuestionRepository
import com.cyrusinnovation.inquisition.response.ResponseRepository
import com.trailmagic.jumper.core.TimeSource
import org.springframework.stereotype.Controller

import scala.collection.JavaConverters._
import util.SecurityHelper
import org.springframework.web.servlet.ModelAndView
import com.cyrusinnovation.inquisition.tags.TagRepository


@Controller
@RequestMapping(value = Array("/questions/{questionId}"))
class ResponseController @Autowired()(questionRepository: QuestionRepository, responseRepository: ResponseRepository, timeSource: TimeSource) {

    @RequestMapping(value = Array("/response"), method = Array(RequestMethod.GET))
    def showNewQuestionResponseForm(@PathVariable questionId: String) = {
        val model = Map("questionId" -> questionId)
        new ModelAndView("new-response", model.asJava)
    }

    @RequestMapping(value = Array("/response"), method = Array(RequestMethod.POST))
    def addResponse(@ModelAttribute qResponse: ResponseFormData, @PathVariable questionId: String) = {
      try
      {
         responseRepository.save(questionId, qResponse.toResponse)
      }
      catch
      {
        case iae: IllegalArgumentException => throw new ResourceNotFoundException()
      }

      "redirect:/questions/" + questionId
    }

}