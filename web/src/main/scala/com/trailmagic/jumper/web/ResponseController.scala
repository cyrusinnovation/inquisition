package com.trailmagic.jumper.web

import model.{QuestionFormData, ResponseFormData}
import org.springframework.web.bind.annotation.{ModelAttribute, RequestMethod, PathVariable, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import com.trailmagic.jumper.core.TimeSource
import org.springframework.stereotype.Controller

import scala.collection.JavaConverters._
import org.springframework.web.servlet.ModelAndView
import com.cyrusinnovation.inquisition.response.{Response, ResponseRepository}
import com.cyrusinnovation.inquisition.questions.{Question, QuestionRepository}
import javax.validation.Valid
import org.springframework.validation.BindingResult
import util.{SecurityHelper, FormHelper}


@Controller
@RequestMapping(value = Array("/questions/{questionId}"))
class ResponseController @Autowired()(responseRepository: ResponseRepository) {

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

    @RequestMapping(value = Array("/edit/response/{responseId}"), method = Array(RequestMethod.GET))
    def editResponse(@PathVariable responseId: String) = {
        responseRepository.getResponse(responseId) match {
            case Some((x: Question, y: Response)) => {
                val mav = new ModelAndView("edit-response")
                mav.addObject("response", new ResponseFormData(y))
                mav.addObject("questionId", x.id.get)
                mav
            }
            case None => {
                throw new ResourceNotFoundException()
            }
        }
    }
    @RequestMapping(value = Array("/edit/response/{responseId}"), method = Array(RequestMethod.PUT))
    def updateResponse(@ModelAttribute response: ResponseFormData,
//                       bindingResult: BindingResult,
                       @PathVariable questionId: String,
                       @PathVariable responseId: String): ModelAndView = {
//        val errors = FormHelper.getAllErrors(bindingResult)
//        if (!errors.isEmpty) {
//            val mav= new ModelAndView("edit-question", "errors", errors)
//            mav.addObject("response", response)
//            mav.addObject("questionId", questionId)
//            mav
//
//        }
        val r = response.toResponse;
        if (!r.id.equals(Some(responseId))) {
            throw new IllegalArgumentException("the responseId did not match the request body's response.id")
        }

        responseRepository.updateResponse(r)

        new ModelAndView("redirect:/questions/" + questionId)
    }

}