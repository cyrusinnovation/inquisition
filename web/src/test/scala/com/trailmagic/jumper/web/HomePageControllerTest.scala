package com.trailmagic.jumper.web

import model.QuestionFormData
import org.mockito.MockitoAnnotations.Mock
import org.mockito.{MockitoAnnotations}
import org.mockito.Mockito._
import util.SecurityHelper
import com.trailmagic.jumper.core.{User, SavedUser, TimeSource}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.joda.time.DateTime
import com.cyrusinnovation.inquisition.tags.TagRepository
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.cyrusinnovation.inquisition.questions.{QuestionService, QuestionRepository}

@RunWith(classOf[JUnitRunner])
class HomePageControllerTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
    val currentUser = new SavedUser("userId", new User("a@example.com", "userName", "firstName", "lastName", "password", "salt", Set(), None))
    @Mock var questionService: QuestionService = _
    @Mock var tagRepository: TagRepository = _

    var controller: HomePageController = _

    override def beforeEach() {

        MockitoAnnotations.initMocks(this)
        controller = new HomePageController(questionService, tagRepository);
        SecurityHelper.setAuthenticatedUser(Some(currentUser))

        val tags = Map[String, Double]()
        when(tagRepository.findMostPopularTags(controller.DEFAULT_NUMBER_OF_TAGS_TO_RETRIEVE)).thenReturn(tags)
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

    test("Canary") {
        questionService should not be (null)
        controller should not be (null)
    }

    test("Verify homepage view name") {
        val mav = controller.showIndex()
        mav.getViewName should equal("index")
    }

    test("Verify homepage view model has correct number of model items") {
        val mav = controller.showIndex()
        mav.getModelMap should have size (4)
    }

    test("Verify view model has user property") {
        val mav = controller.showIndex()
        val model = mav.getModel
        model.containsKey("currentUser") should be(true)
        model.get("currentUser") should be('defined)
        model.get("currentUser").asInstanceOf[Some[SavedUser]].get should equal(currentUser)
    }

    test("Verify view model has tags property") {
        val tags = Map("java" -> 4.0, "spring" -> 1.0)
        when(tagRepository.findMostPopularTags(controller.DEFAULT_NUMBER_OF_TAGS_TO_RETRIEVE)).thenReturn(tags)
        val mav = controller.showIndex()
        val model = mav.getModel
        model.containsKey("tags") should be(true)
        model.get("tags") should not be (null)
        val actual = model.get("tags").asInstanceOf[List[String]]
        actual.head should equal("java")
        actual.tail.head should equal("spring")
    }


    test("Verify view model has tags property in right order") {
        val tags = Map("java" -> 4.0, "scala" -> 3.0, "spring" -> 2.0)
        when(tagRepository.findMostPopularTags(controller.DEFAULT_NUMBER_OF_TAGS_TO_RETRIEVE)).thenReturn(tags)
        val mav = controller.showIndex()
        val model = mav.getModel
        model.containsKey("tags") should be(true)
        model.get("tags") should not be (null)
        val actual = model.get("tags").asInstanceOf[List[String]]

        actual.indexOf("java") should equal(0);
        actual.indexOf("scala") should equal(1);
        actual.indexOf("spring") should equal(2);

    }


    test("Verify view model has questions property") {
        val question = uniqueQuestionFormData().toQuestion(currentUser.username)
        val questions = List(question)
        when(questionService.findRecent()).thenReturn(questions)
        val mav = controller.showIndex()
        val model = mav.getModel
        model.containsKey("questions") should be(true)
        model.get("questions") should not be (null)
        model.get("questions") should equal(questions)
    }

    test("Verify view model has unanswered questions property") {
        val question = uniqueQuestionFormData().toQuestion(currentUser.username)
        val questions = List(question)
        when(questionService.findQuestionsWithoutResponses(10)).thenReturn(questions)
        val mav = controller.showIndex()
        val model = mav.getModel
        model.containsKey("unanswered") should be(true)
        model.get("unanswered") should not be (null)
        model.get("unanswered") should equal(questions)
    }
}

