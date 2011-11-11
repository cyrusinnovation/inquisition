package com.trailmagic.jumper.web

import model.ResponseFormData
import org.springframework.web.bind.annotation.{ModelAttribute, RequestMethod, PathVariable, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

import org.springframework.validation.BindingResult
import util.{SecurityHelper, FormHelper}
import javax.validation.Valid
import com.cyrusinnovation.inquisition.response.ResponseService
import org.springframework.web.servlet.ModelAndView


@Controller
@RequestMapping(value = Array("/questions/{questionId}"))
class ResponseController @Autowired()(responseService: ResponseService) {

    @RequestMapping(value = Array("/response"), method = Array(RequestMethod.GET))
    def showNewQuestionResponseForm(@PathVariable questionId: String) = {
        buildResponseFormModelAndView("new-response", new ResponseFormData(), questionId)
    }

    @RequestMapping(value = Array("/response"), method = Array(RequestMethod.POST))
    def addResponse(@Valid @ModelAttribute response: ResponseFormData,
                    bindingResult: BindingResult,
                    @PathVariable questionId: String): ModelAndView = {
        val errors = FormHelper.getAllErrors(bindingResult)
        if (!errors.isEmpty) {
            return buildResponseFormModelAndView("new-response", response, questionId, errors)
        }

        try {
            val user = SecurityHelper.getMandatoryAuthenticatedUser
            responseService.save(response.toResponse(user.username), questionId, user.username)
        }
        catch {
            case iae: IllegalArgumentException => throw new ResourceNotFoundException()
        }

        new ModelAndView("redirect:/questions/" + questionId)
    }

    @RequestMapping(value = Array("/edit/response/{responseId}"), method = Array(RequestMethod.PUT))
    def updateResponse(@Valid @ModelAttribute response: ResponseFormData,
                       bindingResult: BindingResult,
                       @PathVariable questionId: String,
                       @PathVariable responseId: String): ModelAndView = {
        val errors = FormHelper.getAllErrors(bindingResult)

        if (!errors.isEmpty) {
            return buildResponseFormModelAndView("edit-response", response, questionId, errors)
        }
        val user = SecurityHelper.getMandatoryAuthenticatedUser
        responseService.update(response.toResponse(user.username), SecurityHelper.getMandatoryAuthenticatedUser.username)

        new ModelAndView("redirect:/questions/" + questionId)
    }

    @RequestMapping(value = Array("/edit/response/{responseId}"), method = Array(RequestMethod.GET))
    def showEditResponseForm(@PathVariable responseId: String) = {
        try {
            val response = responseService.findById(responseId)
            val question = responseService.findResponseQuestion(responseId)
            buildResponseFormModelAndView("edit-response", new ResponseFormData(response), question.id.get)
        }
        catch {
            case e: IllegalArgumentException => throw new ResourceNotFoundException
        }
    }

    def buildResponseFormModelAndView(viewName: String, responseFormData: ResponseFormData, questionId: String, errors: Iterable[String] = null): ModelAndView = {
        val mav = new ModelAndView(viewName)
        if (errors != null) {
            mav.addObject("errors", errors)
        }
        mav.addObject("response", responseFormData)
        mav.addObject("questionId", questionId)
        mav
    }
}