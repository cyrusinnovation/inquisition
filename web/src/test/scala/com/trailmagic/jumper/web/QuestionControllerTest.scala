package com.trailmagic.jumper.web

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import org.mockito.MockitoAnnotations.Mock
import org.mockito.Mockito._
import util.SecurityHelper
import com.trailmagic.jumper.core.{User, SavedUser, TimeSource}
import com.cyrusinnovation.inquisition.questions.{Question, QuestionRepository}
import javax.servlet.http.HttpServletResponse
import org.mockito.{Mockito, MockitoAnnotations}
import org.springframework.mock.web.MockHttpServletResponse


class QuestionControllerTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  @Mock var timeSource: TimeSource = _
  @Mock var repository: QuestionRepository = _

  var controller: QuestionController = _

  override def beforeEach() {
    MockitoAnnotations.initMocks(this)
    controller = new QuestionController(repository, timeSource);
    SecurityHelper.setAuthenticatedUser(Some(new SavedUser("userId", new User("a@example.com", "userName",
      "firstName", "lastName", "password", "salt", Set(), None))))
  }

  override def afterEach() {
    SecurityHelper.setAuthenticatedUser(None)
  }

  def uniqueQuestionFormData(title: String = "some new question", body: String = "The question body."):
  QuestionFormData
  = {
    val questionFormData = new QuestionFormData();
    questionFormData.setTitle(title + " " + System.currentTimeMillis())
    questionFormData.setBody(body);
    return questionFormData;
  }

  test("new question should put us on the new question view") {
    controller.showNewQuestionForm() should be("new-question")
  }

  test("create a new question returns to the home page") {
    val q = uniqueQuestionFormData();
    controller.addQuestion(q) should be("redirect:/")
  }

  test("create a new question returns calls the question repository") {
    val q = uniqueQuestionFormData();
    when(repository.save(q.toQuestion)).thenReturn(null);
    controller.addQuestion(q) should be("redirect:/")
  }

  test("show question tests that the view is question") {
    val questionId = "questionId"
    val q = uniqueQuestionFormData();
    when(repository.findById(questionId)).thenReturn(Some(q.toQuestion));
    val mav = controller.showQuestion(questionId)
    mav.getViewName should be("question")
  }


  test("show question tests that the model contains the question") {
    val questionId = "questionId"
    val q = uniqueQuestionFormData();
    when(repository.findById(questionId)).thenReturn(Some(q.toQuestion));
    val mav = controller.showQuestion(questionId)
    mav.getModel.containsKey("question") should be(true)
    mav.getModel.get("question") should be(q.toQuestion)
  }

  test("delete a question")
  {
    val questionId = "questionId"
    val q = uniqueQuestionFormData();

    val viewName = controller.deleteQuestion(questionId)
    verify(repository, times(1)).deleteQuestion(questionId, "userName")
    viewName should be ("redirect:/")
  }

  test("throw a resource not found exception") {
    val questionId = "questionId"
    val q = uniqueQuestionFormData();
    when(repository.findById(questionId)).thenReturn(None);
    evaluating {
      controller.showQuestion(questionId)
    } should produce[ResourceNotFoundException]
  }

  test("search for questions with a tag") {
    val questionReturn = List[Question]()
    val tagFormData = new TagFormData()
    tagFormData.setTagQuery("taga, tagb")
    when(repository.findQuestionsByTags(tagFormData.toTagList)).thenReturn(questionReturn)

    val mav = controller.searchQuestions(tagFormData)
    val model = mav.getModel

    mav.getViewName should be ("search-results")
    model.get("searchTerms") should equal(tagFormData.getTagQuery())
    model.get("questions") should equal(questionReturn)
  }

  test("display new question form") {
    val questionId = "dead6bb0744e9d3695a7f810"
    val question = Some(new Question(None, "Title", "Creator"))
    when(repository.findById(questionId)).thenReturn(question)

    val mav = controller.showNewQuestionResponseForm(questionId)

    mav.getViewName() should be("new-question-response")
    mav.getModelMap.get("questionId") should equal(questionId)
  }

  test("can save new question response") {
    val questionId = "dead6bb0744e9d3695a7f810"
    val questionAnswerModel = new QuestionAnswerFormData()
    questionAnswerModel.setQuestionId(questionId)
    questionAnswerModel.setTitle("Title")
    questionAnswerModel.setBody("Body text")
    val question = new Question(None, "Title", "Creator")

    when(repository.findById(questionId)).thenReturn(Some(question))
    when(repository.saveQuestionAnswer(question, questionAnswerModel.toQuestionAnswer)).thenReturn(question)

    val nextView = controller.addQuestionAnswer(questionAnswerModel)
    nextView should be("redirect:/")
  }

  test("Exception thrown if a question response is added to a non-exstant questions") {
    val questionId = "dead6bb0744e9d3695a7f810"
    val questionAnswerModel = new QuestionAnswerFormData()
    questionAnswerModel.setQuestionId(questionId)
    questionAnswerModel.setTitle("Title")
    questionAnswerModel.setBody("Body text")
    when(repository.findById(questionId)).thenReturn(None)

    evaluating {
      controller.addQuestionAnswer(questionAnswerModel)
    } should produce [ResourceNotFoundException]
  }

    test("Can find tags by prefix") {
      when(repository.findTagsByPrefix("a")).thenReturn(List("a1", "a2"))
      val mav = controller.tagCompletion("a")
      val tagList = mav.getModel.get("tags").asInstanceOf[java.util.List[String]]
      val viewName = mav.getViewName

      viewName should be("tags")
      tagList should have length(2)
      tagList should contain("a1")
      tagList should contain("a2")
    }

    test("Can find tags by prefix that does not exist") {
      when(repository.findTagsByPrefix("a")).thenReturn(List())
      val mav = controller.tagCompletion("a")
      val tagList = mav.getModel.get("tags").asInstanceOf[java.util.List[String]]
      val viewName = mav.getViewName

      viewName should be("tags")
      tagList should have length(0)
    }

    test("Can remove a tag from a question") {
      val response = new MockHttpServletResponse()

      val mav = controller.tagRemoval("questionId", "tagText", response)
      verify(repository, times(1)).deleteTagFromQuestion("questionId", "tagText")

      response.getStatus should equal(204)
    }

  test("Can add a tag to a question") {
      val response = new MockHttpServletResponse()

      val mav = controller.tagAddition("questionId", "tagText", response)
      verify(repository, times(1)).addTagToQuestion("questionId", "tagText")

      response.getStatus should equal(204)
    }
}
