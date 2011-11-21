package com.trailmagic.jumper.web

import util.SecurityHelper
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import scala.collection.JavaConverters._

import org.springframework.web.bind.annotation._
import org.springframework.web.servlet.ModelAndView
import com.cyrusinnovation.inquisition.tags.TagRepository
import com.cyrusinnovation.inquisition.questions.QuestionService

@Controller
class HomePageController @Autowired()(questionService: QuestionService,
                                      tagRepository: TagRepository) {
  final val DEFAULT_NUMBER_OF_TAGS_TO_RETRIEVE: Int = 10

  @RequestMapping(Array("/"))
  def showIndex() = {

    val model = Map("currentUser" -> SecurityHelper.getAuthenticatedUser,
    "questions" -> questionService.findRecent(),
    "tags" -> tagRepository.findMostPopularTags(DEFAULT_NUMBER_OF_TAGS_TO_RETRIEVE).map(x=>x._1).toList,
    "unanswered" -> questionService.findQuestionsWithoutResponses(10))
    new ModelAndView("index", model.asJava)
  }


}