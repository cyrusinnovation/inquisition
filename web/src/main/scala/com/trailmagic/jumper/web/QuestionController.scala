package com.trailmagic.jumper.web

import org.springframework.web.bind.annotation.{ModelAttribute, RequestMethod, PathVariable, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import com.trailmagic.jumper.core.TimeSource
import org.springframework.stereotype.Controller

import scala.collection.JavaConverters._
import service.MarkdownFormattingService
import util.SecurityHelper
import org.springframework.web.servlet.ModelAndView
import com.cyrusinnovation.inquisition.tags.TagRepository

import com.cyrusinnovation.inquisition.questions.{QuestionService, Question}


@Controller
@RequestMapping(value = Array("/questions"))
class QuestionController @Autowired()(timeSource: TimeSource,
                                      tagRepository: TagRepository,
                                      formattingService: MarkdownFormattingService,
                                      questionService: QuestionService) {

  @RequestMapping(value = Array("/new"))
  def showNewQuestionForm(): String = {
    "new-question"
  }


  @RequestMapping(method = Array(RequestMethod.POST))
  def addQuestion(@ModelAttribute question: QuestionFormData) = {
    var q = question.toQuestion;
    val user = SecurityHelper.getMandatoryAuthenticatedUser
    q = q.copy(creatorUsername = user.username);
    val newQuestion = questionService.createQuestion(q)
    "redirect:/questions/" + newQuestion.id.get
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

  @RequestMapping(value = Array("/{questionId}"), method = Array(RequestMethod.PUT))
  def updateQuestion(@ModelAttribute question: QuestionFormData, @PathVariable questionId: String) = {
    var q = question.toQuestion;
    if (!q.id.equals(questionId)) {
      throw new IllegalArgumentException("the questionId did not match the request body's question.id")
    }
    val user = SecurityHelper.getMandatoryAuthenticatedUser
    questionService.updateQuestion(q, user.username)

  }

  @RequestMapping(value = Array("/{questionId}"), method = Array(RequestMethod.DELETE))
  def deleteQuestion(@PathVariable questionId: String) = {
    val user = SecurityHelper.getMandatoryAuthenticatedUser
    questionService.deleteQuestion(questionId, user.username)
    "redirect:/"
  }
}