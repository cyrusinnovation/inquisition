package com.trailmagic.jumper.web

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.mockito.MockitoAnnotations.Mock
import com.cyrusinnovation.inquisition.tags.TagRepository
import org.mockito.MockitoAnnotations
import util.SecurityHelper
import com.trailmagic.jumper.core.{User, SavedUser, TimeSource}
import com.cyrusinnovation.inquisition.questions.{Question, QuestionRepository}
import org.mockito.Mockito._
import com.cyrusinnovation.inquisition.response.ResponseRepository

class ResponseControllerTest  extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
    @Mock var timeSource: TimeSource = _
    @Mock var questionRepository: QuestionRepository = _
    @Mock var responseRepository: ResponseRepository = _
    var controller: ResponseController = _

    override def beforeEach() {
        MockitoAnnotations.initMocks(this)
        controller = new ResponseController(questionRepository, responseRepository, timeSource);
        SecurityHelper.setAuthenticatedUser(Some(new SavedUser("userId", new User("a@example.com", "userName",
            "firstName", "lastName", "password", "salt", Set(), None))))
    }

    override def afterEach() {
        SecurityHelper.setAuthenticatedUser(None)
    }
  test("can display new response form") {
        val questionId = "dead6bb0744e9d3695a7f810"
        val question = Some(new Question(None, "Title", "Creator"))
        when(questionRepository.findById(questionId)).thenReturn(question)

        val mav = controller.showNewQuestionResponseForm(questionId)

        mav.getViewName() should be("new-response")
        mav.getModelMap.get("questionId") should equal(questionId)
    }

    test("can save new question response") {
        val questionId = "dead6bb0744e9d3695a7f810"
        val questionAnswerModel = new QuestionAnswerFormData()
        questionAnswerModel.setQuestionId(questionId)
        questionAnswerModel.setTitle("Title")
        questionAnswerModel.setBody("Body text")
        val question = new Question(None, "Title", "Creator")

        when(questionRepository.findById(questionId)).thenReturn(Some(question))
        when(responseRepository.saveQuestionAnswer(question, questionAnswerModel.toQuestionAnswer)).thenReturn(question)

        val nextView = controller.addQuestionAnswer(questionAnswerModel, questionId)
        nextView should be("redirect:/questions/dead6bb0744e9d3695a7f810")
    }

    test("Exception thrown if a question response is added to a non-exstant questions") {
        val questionId = "dead6bb0744e9d3695a7f810"
        val questionAnswerModel = new QuestionAnswerFormData()
        questionAnswerModel.setQuestionId(questionId)
        questionAnswerModel.setTitle("Title")
        questionAnswerModel.setBody("Body text")
        when(questionRepository.findById(questionId)).thenReturn(None)

        evaluating {
            controller.addQuestionAnswer(questionAnswerModel, questionId)
        } should produce[ResourceNotFoundException]
    }
}