package com.trailmagic.jumper.web

import model.ResponseFormData
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.mockito.MockitoAnnotations.Mock
import com.cyrusinnovation.inquisition.tags.TagRepository
import org.mockito.MockitoAnnotations
import util.SecurityHelper
import com.trailmagic.jumper.core.{User, SavedUser, TimeSource}
import com.cyrusinnovation.inquisition.questions.{Question, QuestionRepository}
import org.mockito.Mockito._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.cyrusinnovation.inquisition.response.{Response, ResponseRepository}
import java.lang.String

@RunWith(classOf[JUnitRunner])
class ResponseControllerTest  extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
    @Mock var responseRepository: ResponseRepository = _
    var controller: ResponseController = _

    override def beforeEach() {
        MockitoAnnotations.initMocks(this)
        controller = new ResponseController(responseRepository);
        SecurityHelper.setAuthenticatedUser(Some(new SavedUser("userId", new User("a@example.com", "userName",
            "firstName", "lastName", "password", "salt", Set(), None))))
    }

    override def afterEach() {
        SecurityHelper.setAuthenticatedUser(None)
    }
  test("can display new response form") {
        val questionId = "dead6bb0744e9d3695a7f810"
        val question = Some(new Question(None, "Title", "Creator"))

        val mav = controller.showNewQuestionResponseForm(questionId)

        mav.getViewName() should be("new-response")
        mav.getModelMap.get("questionId") should equal(questionId)
    }

    test("can save new question response") {
        val questionId = "dead6bb0744e9d3695a7f810"
        val questionAnswerModel = new ResponseFormData()
        questionAnswerModel.setId(questionId)
        questionAnswerModel.setTitle("Title")
        questionAnswerModel.setBody("Body text")
        val question = new Question(Some(questionId), "Title", "Creator")

        when(responseRepository.save(question.id.get, questionAnswerModel.toResponse)).thenReturn(questionAnswerModel.toResponse)

        val nextView = controller.addResponse(questionAnswerModel, questionId)
        nextView should be("redirect:/questions/dead6bb0744e9d3695a7f810")
    }

    test("Exception thrown if a question response is added to a non-exstant questions") {
        val questionId = "dead6bb0744e9d3695a7f810"
        val questionAnswerModel = new ResponseFormData()
        questionAnswerModel.setId(questionId)
        questionAnswerModel.setTitle("Title")
        when(responseRepository.save(questionId, questionAnswerModel.toResponse)).thenThrow(new IllegalArgumentException)

        evaluating {
            controller.addResponse(questionAnswerModel, questionId)
        } should produce[ResourceNotFoundException]
    }

    test("Exception is thrown if trying to edit non-existant response") {
        when(responseRepository.getResponse("0")).thenReturn(Option.empty)
        evaluating {
            controller.editResponse("0")
        } should produce[ResourceNotFoundException]
    }
    test("Can edit a response") {
        val response = Response(None, "Title", "username", "Body")
        val question = Question(Some("dead6bb0744e9d3695a7f810"), creatorUsername = "tester", title="Title")
        when(responseRepository.getResponse("dead6bb0744e9d3695a7f810")).thenReturn(Some(question, response))
        val mav = controller.editResponse("dead6bb0744e9d3695a7f810")
        val expectedResponse = mav.getModel.get("response").asInstanceOf[ResponseFormData]
        compareResponseFormData(expectedResponse, new ResponseFormData(response)) should be(true)
        mav.getModel.get("questionId") should be("dead6bb0744e9d3695a7f810")
    }
    test("Can submit and save an edited response") {
        val responseId: String = "responseid"
        val responseForm: ResponseFormData = new ResponseFormData()
        responseForm.setId(responseId)
        controller.updateResponse(responseForm,"questionid", responseId)

    }
    def compareResponseFormData(rfda: ResponseFormData, rfdb: ResponseFormData): Boolean = {
        if (rfda == null || rfdb == null) {
            return rfda == rfdb;
        }
        if (rfda.getBody() != rfdb.getBody()) return false;
        if (rfda.getTitle() != rfdb.getTitle()) return false;
        if (rfda.getId() != rfdb.getId()) return false;

        return true;
    }
}