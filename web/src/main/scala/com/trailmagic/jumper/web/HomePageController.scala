package com.trailmagic.jumper.web

import util.SecurityHelper
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import com.trailmagic.jumper.core.TimeSource
import scala.collection.JavaConverters._
import com.cyrusinnovation.inquisition.questions.QuestionRepository
import org.springframework.web.bind.annotation._
import org.springframework.web.servlet.ModelAndView
import org.slf4j._
import com.cyrusinnovation.inquisition.tags.TagRepository

@Controller
class HomePageController @Autowired()(questionRepository: QuestionRepository, timeSource: TimeSource,
                                      tagRepository: TagRepository) {
  final val DEFAULT_NUMBER_OF_TAGS_TO_RETRIEVE: Int = 10

  @RequestMapping(Array("/"))
  def showIndex() = {

    val model = Map("currentUser" -> SecurityHelper.getAuthenticatedUser,
    "questions" -> questionRepository.findRecent(timeSource.now),
    "tags" -> tagRepository.findMostPopularTags(DEFAULT_NUMBER_OF_TAGS_TO_RETRIEVE))
    new ModelAndView("index", model.asJava)
  }


}