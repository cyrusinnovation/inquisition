package com.trailmagic.jumper.web

import model.ResponseFormData
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.mockito.MockitoAnnotations.Mock
import org.mockito.MockitoAnnotations
import util.SecurityHelper
import com.trailmagic.jumper.core.{User, SavedUser, TimeSource}
import com.cyrusinnovation.inquisition.questions.{Question, QuestionRepository}
import org.mockito.Mockito._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.lang.String
import org.springframework.validation.BindingResult
import com.cyrusinnovation.inquisition.response.{ResponseService, Response, ResponseRepository}
import com.cyrusinnovation.inquisition.response.service.ResponseServiceImpl
import java.security.InvalidParameterException

@RunWith(classOf[JUnitRunner])
class ResponseControllerTest  extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
    @Mock var responseRepository: ResponseRepository = _
    @Mock var bindingResult: BindingResult = _
    var controller: ResponseController = _
    var service: ResponseService = _
    override def beforeEach() {
        MockitoAnnotations.initMocks(this)
        service = new ResponseServiceImpl(responseRepository)
        controller = new ResponseController(service);
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
    test("when a new question is saved it has the appropriate username")  {
    val questionId = "dead6bb0744e9d3695a7f810"
        val questionAnswerModel = new ResponseFormData()
        questionAnswerModel.setId(questionId)
        questionAnswerModel.setTitle("Title")
        questionAnswerModel.setBody("Body text")
        val question = new Question(Some(questionId), "Title", "Creator")

        val response: Response = questionAnswerModel.toResponse("testUser")
        when(responseRepository.save(question.id.get, response)).thenReturn(response)

        val nextView = controller.addResponse(questionAnswerModel, bindingResult, questionId)
        nextView.getViewName should be("redirect:/questions/dead6bb0744e9d3695a7f810")
    }
    test("can save new question response") {
        val questionId = "dead6bb0744e9d3695a7f810"
        val questionAnswerModel = new ResponseFormData()
        questionAnswerModel.setId(questionId)
        questionAnswerModel.setTitle("Title")
        questionAnswerModel.setBody("Body text")
        val question = new Question(Some(questionId), "Title", "Creator")

        val response: Response = questionAnswerModel.toResponse("testUser")
        when(responseRepository.save(question.id.get, response)).thenReturn(response)

        val nextView = controller.addResponse(questionAnswerModel, bindingResult, questionId)
        nextView.getViewName should be("redirect:/questions/dead6bb0744e9d3695a7f810")
    }

    test("Exception thrown if a question response is added to a non-exstant questions") {
        val questionId = "dead6bb0744e9d3695a7f810"
        val questionAnswerModel = new ResponseFormData()
        questionAnswerModel.setId(questionId)
        questionAnswerModel.setTitle("Title")

        when(responseRepository.save(questionId, questionAnswerModel.toResponse("userName"))).thenThrow(new IllegalArgumentException)

        evaluating {
            controller.addResponse(questionAnswerModel, bindingResult, questionId)
        } should produce[ResourceNotFoundException]
    }

    test("Exception is thrown if trying to edit non-existant response") {
        when(responseRepository.getResponse("0")).thenReturn(Option.empty)
        evaluating {
            controller.showEditResponseForm("0")
        } should produce[ResourceNotFoundException]
    }
    test("Can edit a response") {
        val response = Response(None, "Title", "username", "Body")
        val question = Question(Some("dead6bb0744e9d3695a7f810"), creatorUsername = "tester", title="Title")
        when(responseRepository.getResponse("dead6bb0744e9d3695a7f810")).thenReturn(Some(question, response))
        val mav = controller.showEditResponseForm("dead6bb0744e9d3695a7f810")
        val expectedResponse = mav.getModel.get("response").asInstanceOf[ResponseFormData]
        compareResponseFormData(expectedResponse, new ResponseFormData(response)) should be(true)
        mav.getModel.get("questionId") should be("dead6bb0744e9d3695a7f810")
    }

    test("Can submit and save an edited response") {
        val responseId: String = "responseid"
        val responseForm: ResponseFormData = new ResponseFormData()
        responseForm.setId(responseId)
        val responseTuple = Some((null, responseForm.toResponse("userName")))
        when(responseRepository.getResponse(responseId)).thenReturn(responseTuple)
        controller.updateResponse(responseForm, bindingResult, "questionid", responseId)
    }

    test("Only response creator can edit a response") {
        val response = Response(Some("dead6bb0744e9d3695a7f810"), "Title", "username", "Body")
        val question = Question(Some("dead6bb0744e9d3695a7f810"), creatorUsername = "tester", title="Title")
        when(responseRepository.getResponse("dead6bb0744e9d3695a7f810")).thenReturn(Some(question, response))
        val rfd =new ResponseFormData(response)
        evaluating{
            controller.updateResponse(rfd, bindingResult, "dead6bb0744e9d3695a7f810", "dead6bb0744e9d3695a7f810")
        } should produce[InvalidParameterException]
    }

    def compareResponseFormData(leftResponse: ResponseFormData, rightResponse: ResponseFormData): Boolean = {
        if (leftResponse == null || rightResponse == null) {
            return leftResponse == rightResponse;
        }
        if (leftResponse.getBody() != rightResponse.getBody()) return false;
        if (leftResponse.getTitle() != rightResponse.getTitle()) return false;
        if (leftResponse.getId() != rightResponse.getId()) return false;

        return true;
    }
}