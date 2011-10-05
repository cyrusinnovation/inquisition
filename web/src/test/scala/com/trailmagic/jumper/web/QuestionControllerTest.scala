package com.trailmagic.jumper.web

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import org.mockito.MockitoAnnotations.Mock
import org.mockito.MockitoAnnotations

import com.cyrusinnovation.inquisition.questions.QuestionRepository
import org.mockito.Mockito._
import java.security.Principal
import util.SecurityHelper
import com.trailmagic.jumper.core.{User, SavedUser, TimeSource}

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

  def uniqueQuestionFormData(title: String = "How do I use MongoDB?"): QuestionFormData = {
    val questionFormData = new QuestionFormData();
    questionFormData.setTitle(title + " " + System.currentTimeMillis())
    questionFormData.setBody("The question body.");
    return questionFormData;
  }

  test("new question should put us on the new question view") {
    controller.showNewQuestionForm() should be("new-question")
  }

  test("create a new question returns to the home page") {
    val q = uniqueQuestionFormData("some new question");
    controller.addQuestion(q) should be("redirect:/")
  }

  test("create a new question returns calls the question repository") {
    val q = uniqueQuestionFormData("some new question");
    when(repository.save(q.toQuestion)).thenReturn(null);
    controller.addQuestion(q) should be("redirect:/")
  }

  test("show question tests that the view is question") {
    val questionId = "questionId"
    val q = uniqueQuestionFormData("some new question");
    when(repository.findById(questionId)).thenReturn(Some(q.toQuestion));
    val mav = controller.showQuestion(questionId)
    mav.getViewName should be("question")
  }


  test("show question tests that the model contains the question") {
    val questionId = "questionId"
    val q = uniqueQuestionFormData("some new question");
    when(repository.findById(questionId)).thenReturn(Some(q.toQuestion));
    val mav = controller.showQuestion(questionId)
    mav.getModel.containsKey("question") should be(true)
    mav.getModel.get("question") should be(q.toQuestion)
  }


  test("throw a resource not found exception") {
    val questionId = "questionId"
    val q = uniqueQuestionFormData("some new question");
    when(repository.findById(questionId)).thenReturn(None);
    evaluating {
      controller.showQuestion(questionId)
    } should produce[ResourceNotFoundException]
  }
}