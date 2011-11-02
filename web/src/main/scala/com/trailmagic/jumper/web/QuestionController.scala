package com.trailmagic.jumper.web

import org.springframework.web.bind.annotation.{ModelAttribute, RequestMethod, PathVariable, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import com.trailmagic.jumper.core.TimeSource
import org.springframework.stereotype.Controller

import scala.collection.JavaConverters._
import service.MarkdownFormattingService
import util.SecurityHelper
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import com.cyrusinnovation.inquisition.tags.TagRepository
import com.cyrusinnovation.inquisition.questions.{Question, QuestionRepository}


@Controller
@RequestMapping(value = Array("/questions"))
class QuestionController @Autowired()(questionRepository: QuestionRepository,
                                      timeSource: TimeSource,
                                      tagRepository: TagRepository,
                                      formattingService: MarkdownFormattingService) {

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

  def formatText(text: String): String = {
    val encodedBodyText = formattingService.encodeHtmlBrackets(text)
    formattingService.formatMarkdownAsHtmlBlock(encodedBodyText)
  }

  def formatQuestion(question: Question): Question = {
    val formattedBodyText: String = formatText(question.body)
    val formattedResponses = question.responses.map(x => x.copy(body = formatText(x.body)))
    question.copy(body = formattedBodyText, responses = formattedResponses);
  }

  @RequestMapping(value = Array("/{questionId}"), method = Array(RequestMethod.GET))
    def showQuestion(@PathVariable questionId: String) = {
        questionRepository.findById(questionId) match {
            case Some(question) => {
                val model = Map("question" -> formatQuestion(question))
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
}