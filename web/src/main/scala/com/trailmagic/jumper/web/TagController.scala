package com.trailmagic.jumper.web

import model.TagFormData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

import scala.collection.JavaConverters._

import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import com.cyrusinnovation.inquisition.tags.TagRepository
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation._


@Controller
class TagController @Autowired()(tagRepository: TagRepository) {

  @RequestMapping(value = Array("/questions/search"), method = Array(RequestMethod.POST))
  def findQuestionsByTags(@ModelAttribute tags: TagFormData) = {
    val tagList = tags.toTagList
    val questions = tagRepository.findQuestionsByTags(tagList)
    val model = Map("questions" -> questions,
      "searchTerms" -> tags.getTagQuery())
    new ModelAndView("search-results", model.asJava)
  }

  @RequestMapping(value = Array("/questions/tags/{prefix}"), method = Array(RequestMethod.GET))
  def tagCompletion(@PathVariable prefix: String) = {
    val tags = tagRepository.findTagsByPrefix(prefix)
    val model = Map("tags" -> tags.asJava)
    new ModelAndView("tags", model.asJava)
  }

  @RequestMapping(value = Array("/questions/{questionId}/tags/{tagText}"), method = Array(RequestMethod.DELETE))
  def tagRemoval(@PathVariable questionId: String, @PathVariable tagText: String, response: HttpServletResponse) {
    tagRepository.deleteTagFromQuestion(questionId, tagText)
    response.setStatus(HttpStatus.NO_CONTENT.value())
  }

    @RequestMapping(value = Array("/questions/{questionId}/tags"), method = Array(RequestMethod.POST))
    def tagAddition(@RequestParam tagsww: String, @PathVariable questionId: String) = {
      tagRepository.addTagToQuestion(questionId, tagsww)
      new ModelAndView("redirect:/questions/" + questionId)
  }
}