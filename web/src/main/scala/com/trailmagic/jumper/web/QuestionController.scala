package com.trailmagic.jumper.web

import org.springframework.web.bind.annotation.{ModelAttribute, RequestMethod, PathVariable, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import com.cyrusinnovation.inquisition.questions.QuestionRepository
import com.trailmagic.jumper.core.TimeSource
import org.springframework.stereotype.Controller

import scala.collection.JavaConverters._
import util.SecurityHelper
import org.springframework.web.servlet.ModelAndView

@Controller
class QuestionController @Autowired()(questionRepository: QuestionRepository, timeSource: TimeSource) {


    @RequestMapping(Array("/questions/new"))
    def showNewQuestionForm(): String = {
        "new-question"
    }


    @RequestMapping(value = Array("/questions"), method = Array(RequestMethod.POST))
    def addQuestion(@ModelAttribute question: QuestionFormData) = {
        var q = question.toQuestion;
        val user = SecurityHelper.getMandatoryAuthenticatedUser
        q = q.copy(creatorUsername = user.username);
        questionRepository.save(q)
        "redirect:/"
    }

    @RequestMapping(value = Array("/questions/{questionId}"), method = Array(RequestMethod.GET))
    def showQuestion(@PathVariable questionId: String) = {
        questionRepository.findById(questionId) match {
            case Some(question) => {
                val model = Map("question" -> question)
                new ModelAndView("question", model.asJava)
            }
            case None => throw new ResourceNotFoundException
        }
    }

    @RequestMapping(value = Array("/questions/{questionId}"), method = Array(RequestMethod.DELETE))
    def deleteQuestion(@PathVariable questionId: String) = {
        val user = SecurityHelper.getMandatoryAuthenticatedUser
        questionRepository.deleteQuestion(questionId, user.username)
        "redirect:/"
    }

    @RequestMapping(value = Array("/questions/search"), method = Array(RequestMethod.POST))
    def searchQuestions(@ModelAttribute tags: TagFormData) = {
        val tagList = tags.toTagList
        val questions = questionRepository.findQuestionsByTags(tagList)
        val model = Map("questions" -> questions,
            "searchTerms" -> tags.getTagQuery())
        new ModelAndView("search-results", model.asJava)
    }

    @RequestMapping(Array("/questions/newResponse/{questionId}"))
    def showNewQuestionResponseForm(@PathVariable questionId: String) = {
        val model = Map("questionId" -> questionId)
        new ModelAndView("new-question-response", model.asJava)
    }

    @RequestMapping(value = Array("/questions/newResponse"), method = Array(RequestMethod.POST))
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

    @RequestMapping(value =Array("/questions/tags/{prefix}"), method = Array(RequestMethod.GET))
    def tagCompletion(@PathVariable prefix: String) = {
      val tags = questionRepository.findTagsByPrefix(prefix)
      val model = Map("tags" -> tags.asJava)
      new ModelAndView("tags", model.asJava)
    }
}