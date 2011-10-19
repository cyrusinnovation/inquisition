package com.trailmagic.jumper.web

import org.springframework.web.bind.annotation.{ModelAttribute, RequestMethod, PathVariable, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import com.cyrusinnovation.inquisition.questions.QuestionRepository
import com.trailmagic.jumper.core.TimeSource
import org.springframework.stereotype.Controller

import scala.collection.JavaConverters._
import util.SecurityHelper
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import com.cyrusinnovation.inquisition.tags.TagRepository


@Controller
@RequestMapping(value = Array("/questions"))
class QuestionController @Autowired()(questionRepository: QuestionRepository, timeSource: TimeSource,
                                      tagRepository: TagRepository) {

    @RequestMapping(value = Array("/new"))
    def showNewQuestionForm(): String = {
        "new-question"
    }


    @RequestMapping(method = Array(RequestMethod.POST))
    def addQuestion(@ModelAttribute question: QuestionFormData) = {
        var q = question.toQuestion;
        val user = SecurityHelper.getMandatoryAuthenticatedUser
        q = q.copy(creatorUsername = user.username);
        questionRepository.save(q)
        "redirect:/"
    }

    @RequestMapping(value = Array("/{questionId}"), method = Array(RequestMethod.GET))
    def showQuestion(@PathVariable questionId: String) = {
        questionRepository.findById(questionId) match {
            case Some(question) => {
                val model = Map("question" -> question)
                new ModelAndView("question", model.asJava)
            }
            case None => throw new ResourceNotFoundException
        }
    }

    @RequestMapping(value = Array("/{questionId}"), method = Array(RequestMethod.DELETE))
    def deleteQuestion(@PathVariable questionId: String) = {
        val user = SecurityHelper.getMandatoryAuthenticatedUser
        questionRepository.deleteQuestion(questionId, user.username)
        "redirect:/"
    }

    @RequestMapping(value = Array("/newResponse/{questionId}"), method = Array(RequestMethod.GET))
    def showNewQuestionResponseForm(@PathVariable questionId: String) = {
        val model = Map("questionId" -> questionId)
        new ModelAndView("new-question-response", model.asJava)
    }

    @RequestMapping(value = Array("/newResponse"), method = Array(RequestMethod.POST))
    def addQuestionAnswer(@ModelAttribute questionAnswer: QuestionAnswerFormData) = {
        val question = questionRepository.findById(questionAnswer.getQuestionId())
        match {
            case Some(question) => {
                questionRepository.saveQuestionAnswer(question, questionAnswer.toQuestionAnswer)
            }
            case None => throw new ResourceNotFoundException
        }
        "redirect:/"
    }

}