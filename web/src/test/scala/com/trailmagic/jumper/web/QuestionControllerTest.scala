package com.trailmagic.jumper.web

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import org.mockito.MockitoAnnotations.Mock
import org.mockito.Mockito._
import service.MarkdownFormattingService
import util.SecurityHelper
import com.trailmagic.jumper.core.{User, SavedUser, TimeSource}
import com.cyrusinnovation.inquisition.questions.{Question, QuestionRepository}
import org.mockito.MockitoAnnotations
import org.springframework.mock.web.MockHttpServletResponse
import com.cyrusinnovation.inquisition.tags.TagRepository
import com.cyrusinnovation.inquisition.response.Response
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class QuestionControllerTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
    val formattingService = new MarkdownFormattingService;
    @Mock var timeSource: TimeSource = _
    @Mock var questionRepository: QuestionRepository = _
    @Mock var tagRepository: TagRepository = _

    var controller: QuestionController = _

    override def beforeEach() {
        MockitoAnnotations.initMocks(this)
        controller = new QuestionController(questionRepository, timeSource, tagRepository, formattingService);
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
        when(questionRepository.save(q.toQuestion)).thenReturn(null);
        controller.addQuestion(q) should be("redirect:/")
    }

    test("show question tests that the view is question") {
        val questionId = "questionId"
        val q = uniqueQuestionFormData();
        when(questionRepository.findById(questionId)).thenReturn(Some(q.toQuestion));
        val mav = controller.showQuestion(questionId)
        mav.getViewName should be("question")
    }


    test("show question tests that the model contains the question") {
        val questionId = "questionId"
        val q = uniqueQuestionFormData().toQuestion;
        when(questionRepository.findById(questionId)).thenReturn(Some(q));
        val mav = controller.showQuestion(questionId)
        mav.getModel.containsKey("question") should be(true)
      val expectedQuestion: Question = q.copy(body = formattingService.formatMarkdownAsHtmlBlock(q.body))
      mav.getModel.get("question") should be(expectedQuestion)
    }

    test("delete a question") {
        val questionId = "questionId"
        val q = uniqueQuestionFormData();

        val viewName = controller.deleteQuestion(questionId)
        verify(questionRepository, times(1)).deleteQuestion(questionId, "userName")
        viewName should be("redirect:/")
    }

    test("throw a resource not found exception") {
        val questionId = "questionId"
        val q = uniqueQuestionFormData();
        when(questionRepository.findById(questionId)).thenReturn(None);
        evaluating {
            controller.showQuestion(questionId)
        } should produce[ResourceNotFoundException]
    }

  test("question text is the same when no formatting present") {

      val q = uniqueQuestionFormData();
      val actual = controller.formatQuestion(q.toQuestion);
      actual.body should equal(formattingService.formatMarkdownAsHtmlBlock( q.getBody()))
  }

  test("question text is the formatted when formatting present") {

      val q = uniqueQuestionFormData(body = "*test*");
    val expected = "<p><em>test</em></p>"
      val actual = controller.formatQuestion(q.toQuestion);
      actual.body should equal(expected)
  }

  test("question text is the formatted when html present") {

      val q = uniqueQuestionFormData(body = "*test<html>*");
    val expected = "<p><em>test<html></em></p>"
      val actual = controller.formatQuestion(q.toQuestion);
      actual.body should equal(expected)
  }

  test("unformatted question responses stay unformatted") {
    val responses = List(Response(None, creatorUsername = "user" , title="title" , body="text"),
                         Response(None, creatorUsername = "user" , title="title" , body="text2"))
    val q = uniqueQuestionFormData().toQuestion.copy(responses = responses)

    controller.formatQuestion(q)

    q should equal(q)
  }

  test("Formatted question responses has html entities encoded") {
    val responses = List(Response(None, creatorUsername = "user" , title="title" , body="*<html>test*"),
                         Response(None, creatorUsername = "user" , title="title" , body="*<html>test2*"))
    val q = uniqueQuestionFormData().toQuestion.copy(responses = responses)

    val formattedQuestions = controller.formatQuestion(q)
    formattedQuestions.responses.head.body should equal("<p><em><html>test</em></p>")
    formattedQuestions.responses.last.body should equal("<p><em><html>test2</em></p>")
  }

  test("Formatted question responses stay gets formatted") {
    val responses = List(Response(None, creatorUsername = "user" , title="title" , body="*test*"),
                         Response(None, creatorUsername = "user" , title="title" , body="*test2*"))

    val q = uniqueQuestionFormData().toQuestion.copy(responses = responses)

    val formattedQuestions = controller.formatQuestion(q)
    formattedQuestions.responses.head.body should equal("<p><em>test</em></p>")
    formattedQuestions.responses.last.body should equal("<p><em>test2</em></p>")
  }
}
