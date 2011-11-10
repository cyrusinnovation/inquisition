package com.trailmagic.jumper.web

import model.ResponseFormData
import org.springframework.web.bind.annotation.{ModelAttribute, RequestMethod, PathVariable, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

import scala.collection.JavaConverters._
import com.cyrusinnovation.inquisition.response.{Response, ResponseRepository}
import com.cyrusinnovation.inquisition.questions.Question
import util.FormHelper
import org.springframework.web.servlet.ModelAndView
import org.springframework.validation.BindingResult
import javax.validation.Valid


@Controller
@RequestMapping(value = Array("/questions/{questionId}"))
class ResponseController @Autowired()(responseRepository: ResponseRepository) {

    @RequestMapping(value = Array("/response"), method = Array(RequestMethod.GET))
    def showNewQuestionResponseForm(@PathVariable questionId: String) = {
        val model = Map("questionId" -> questionId)
        new ModelAndView("new-response", model.asJava)
    }

    @RequestMapping(value = Array("/response"), method = Array(RequestMethod.POST))
    def addResponse(@Valid @ModelAttribute qResponse: ResponseFormData, bindingResult: BindingResult,
                    @PathVariable questionId: String): ModelAndView = {
        val errors = FormHelper.getAllErrors(bindingResult)
        if (!errors.isEmpty) {
            return new ModelAndView("new-response", "errors", errors).addObject("response",
                qResponse).addObject("questionId", questionId)

        }

        try {
            responseRepository.save(questionId, qResponse.toResponse)
        }
        catch {
            case iae: IllegalArgumentException => throw new ResourceNotFoundException()
        }

        new ModelAndView("redirect:/questions/" + questionId)
    }

    @RequestMapping(value = Array("/edit/response/{responseId}"), method = Array(RequestMethod.GET))
    def editResponse(@PathVariable responseId: String) = {
        responseRepository.getResponse(responseId) match {
            case Some((question: Question, response: Response)) => {
                val mav = new ModelAndView("edit-response")
                mav.addObject("response", new ResponseFormData(response))
                mav.addObject("questionId", question.id.get)
                mav
            }
            case None => {
                throw new ResourceNotFoundException()
            }
        }
    }

    @RequestMapping(value = Array("/edit/response/{responseId}"), method = Array(RequestMethod.PUT))
    def updateResponse(@Valid @ModelAttribute response: ResponseFormData,
                       bindingResult: BindingResult,
                       @PathVariable questionId: String,
                       @PathVariable responseId: String): ModelAndView = {
        val errors = FormHelper.getAllErrors(bindingResult)
        if (!errors.isEmpty) {
            return new ModelAndView("edit-response", "errors", errors).addObject("response",
                response).addObject("questionId", questionId)
        }
        val r = response.toResponse;
        if (!r.id.equals(Some(responseId))) {
            throw new IllegalArgumentException("the responseId did not match the request body's response.id")
        }

        responseRepository.updateResponse(r)

        new ModelAndView("redirect:/questions/" + questionId)
    }

}