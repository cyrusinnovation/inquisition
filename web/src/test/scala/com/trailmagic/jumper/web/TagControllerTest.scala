package com.trailmagic.jumper.web

import model.{TagFormData, QuestionFormData}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import org.mockito.MockitoAnnotations.Mock
import org.mockito.Mockito._
import com.cyrusinnovation.inquisition.questions.Question
import org.mockito.MockitoAnnotations
import org.springframework.mock.web.MockHttpServletResponse
import com.cyrusinnovation.inquisition.tags.TagRepository
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TagControllerTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  @Mock var tagRepository: TagRepository = _

  var controller: TagController = _

  override def beforeEach() {
    MockitoAnnotations.initMocks(this)
    controller = new TagController(tagRepository);

  }

  def uniqueQuestionFormData(title: String = "some new question", body: String = "The question body."):
  QuestionFormData = {
    val questionFormData = new QuestionFormData();
    questionFormData.setTitle(title + " " + System.currentTimeMillis())
    questionFormData.setBody(body);
    questionFormData;
  }


  test("search for questions with a tag") {
    val questionReturn = List[Question]()
    val tagFormData = new TagFormData()
    tagFormData.setTagQuery("taga, tagb")
    when(tagRepository.findQuestionsByTags(tagFormData.toTagList)).thenReturn(questionReturn)

    val mav = controller.findQuestionsByTags(tagFormData)
    val model = mav.getModel

    mav.getViewName should be("search-results")
    model.get("searchTerms") should equal(tagFormData.getTagQuery())
    model.get("questions") should equal(questionReturn)
  }

  test("Can find tags by prefix") {
    when(tagRepository.findTagsByPrefix("a")).thenReturn(List("a1", "a2"))
    val mav = controller.tagCompletion("a")
    val tagList = mav.getModel.get("tags").asInstanceOf[java.util.List[String]]
    val viewName = mav.getViewName

    viewName should be("tags")
    tagList should have length (2)
    tagList should contain("a1")
    tagList should contain("a2")
  }

  test("Can find tags by prefix that does not exist") {
    when(tagRepository.findTagsByPrefix("a")).thenReturn(List())
    val mav = controller.tagCompletion("a")
    val tagList = mav.getModel.get("tags").asInstanceOf[java.util.List[String]]
    val viewName = mav.getViewName

    viewName should be("tags")
    tagList should have length (0)
  }

  test("Can remove a tag from a question") {
    val response = new MockHttpServletResponse()

    controller.tagRemoval("questionId", "tagText", response)
    verify(tagRepository, times(1)).deleteTagFromQuestion("questionId", "tagText")

    response.getStatus should equal(204)
  }

  test("Can add a tag to a question") {
    val tagFormData = new TagFormData()
    tagFormData.setTagQuery("tagText")
    val mav = controller.tagAddition("tagText", "questionId")
    verify(tagRepository, times(1)).addTagToQuestion("questionId", "tagText")

    mav.getViewName should equal("redirect:/questions/questionId")
  }
}
