package com.trailmagic.jumper.web

import model.QuestionFormData
import org.springframework.web.bind.annotation.{ModelAttribute, RequestMethod, PathVariable, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

import scala.collection.JavaConverters._
import service.MarkdownFormattingService

import com.cyrusinnovation.inquisition.questions.{QuestionService, Question}
import javax.validation.Valid
import org.springframework.validation.BindingResult
import util.{FormHelper, SecurityHelper}
import org.springframework.web.servlet.ModelAndView


@Controller
@RequestMapping(value = Array("/questions"))
class QuestionController @Autowired()(formattingService: MarkdownFormattingService,
                                      questionService: QuestionService) {


    @RequestMapping(value = Array("/new"))
    def showNewQuestionForm(): ModelAndView = {
        new ModelAndView("new-question", "clients", questionService.getClientList(limit = 10))
    }


    @RequestMapping(method = Array(RequestMethod.POST))
    def addQuestion(@Valid @ModelAttribute question: QuestionFormData, bindingResult: BindingResult): ModelAndView = {
        val errors = FormHelper.getAllErrors(bindingResult)
        if (!errors.isEmpty) {
            return new ModelAndView("new-question", "errors", errors).addObject("question", question)

        }
        var q = question.toQuestion;
        val user = SecurityHelper.getMandatoryAuthenticatedUser
        q = q.copy(creatorUsername = user.username);
        val newQuestion = questionService.createQuestion(q)
        new ModelAndView("redirect:/questions/" + newQuestion.id.get)
    }

    @RequestMapping(value = Array("/edit/{questionId}"))
    def showEditQuestionForm(@PathVariable questionId: String) = {
        try {
            val question = questionService.findById(questionId)
            val model = Map("question" -> new QuestionFormData(question), "clients" -> questionService.getClientList
            (limit = 10))
            new ModelAndView("edit-question", model.asJava)
        }
        catch {
            case e: IllegalArgumentException => throw new ResourceNotFoundException
        }

    }

    def formatText(text: String): String = {
        formattingService.formatMarkdownAsHtmlBlock(text)
    }

    def formatQuestion(question: Question): Question = {
        val formattedBodyText: String = formatText(question.body)
        val formattedResponses = question.responses.map(x => x.copy(body = formatText(x.body)))
        question.copy(body = formattedBodyText, responses = formattedResponses);
    }

    @RequestMapping(value = Array("/{questionId}"), method = Array(RequestMethod.GET))
    def showQuestion(@PathVariable questionId: String) = {
        try {
            val question = questionService.findById(questionId)
            val model = Map("question" -> formatQuestion(question))
            new ModelAndView("question", model.asJava)
        }
        catch {
            case e: IllegalArgumentException => throw new ResourceNotFoundException
        }
    }

    @RequestMapping(value = Array("/edit/{questionId}"), method = Array(RequestMethod.PUT))
    def updateQuestion(@Valid @ModelAttribute question: QuestionFormData,
                       bindingResult: BindingResult, @PathVariable questionId: String): ModelAndView = {
        val errors = FormHelper.getAllErrors(bindingResult)
        if (!errors.isEmpty) {
            return new ModelAndView("edit-question", "errors", errors).addObject("question", question)

        }
        val q = question.toQuestion;
        if (!q.id.equals(Some(questionId))) {
            throw new IllegalArgumentException("the questionId did not match the request body's question.id")
        }

        val user = SecurityHelper.getMandatoryAuthenticatedUser
        questionService.updateQuestion(q, user.username)
        new ModelAndView("redirect:/questions/" + questionId)
    }

    @RequestMapping(value = Array("/{questionId}"), method = Array(RequestMethod.DELETE))
    def deleteQuestion(@PathVariable questionId: String) = {
        val user = SecurityHelper.getMandatoryAuthenticatedUser
        questionService.deleteQuestion(questionId, user.username)
        "redirect:/"
    }

    @RequestMapping(value = Array("/questions/clients/{prefix}"), method = Array(RequestMethod.GET))
    def clientCompletion(@PathVariable prefix: String) = {
        val clients = questionService.getClientList(prefix)
        val model = Map("clients" -> clients.asJava)
        new ModelAndView("clients", model.asJava)
    }
}