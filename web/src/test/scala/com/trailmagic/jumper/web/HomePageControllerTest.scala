package com.trailmagic.jumper.web

import com.cyrusinnovation.inquisition.questions.QuestionRepository
import org.mockito.MockitoAnnotations.Mock
import org.mockito.{MockitoAnnotations}
import org.mockito.Mockito._
import util.SecurityHelper
import com.trailmagic.jumper.core.{User, SavedUser, TimeSource}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.joda.time.DateTime
import com.cyrusinnovation.inquisition.tags.TagRepository

class HomePageControllerTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  val currentUser = new SavedUser("userId", new User("a@example.com", "userName", "firstName", "lastName", "password", "salt", Set(), None))
  @Mock var timeSource: TimeSource = _
  @Mock var questionRepository: QuestionRepository = _
  @Mock var tagRepository: TagRepository = _

  var controller: HomePageController = _

  override def beforeEach() {
    MockitoAnnotations.initMocks(this)
    controller = new HomePageController(questionRepository, timeSource, tagRepository);
    SecurityHelper.setAuthenticatedUser(Some(currentUser))
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
    timeSource should not be (null)
    questionRepository should not be (null)
    controller should not be (null)
  }

  test("Verify homepage view name") {
    val mav = controller.showIndex()
    mav.getViewName should equal("index")
  }

  test("Verify homepage view model has correct number of model items") {
    val mav = controller.showIndex()
    mav.getModelMap should have size (3)
  }

  test("Verify view model has user property") {
    val mav = controller.showIndex()
    val model = mav.getModel
    model.containsKey("currentUser") should be(true)
    model.get("currentUser") should be('defined)
    model.get("currentUser").asInstanceOf[Some[SavedUser]].get should equal(currentUser)
  }

  test("Verify view model has tags property") {
    val tags = List(("java", 4), ("spring", 1))
    when(tagRepository.findMostPopularTags(controller.DEFAULT_NUMBER_OF_TAGS_TO_RETRIEVE)).thenReturn(tags)
    val mav = controller.showIndex()
    val model = mav.getModel
    model.containsKey("tags") should be(true)
    model.get("tags") should not be (null)
    model.get("tags") should equal(tags)
  }


  test("Verify view model has questions property") {
    val question = uniqueQuestionFormData().toQuestion
    val questions = List(question)
    val now = new DateTime()
    when(timeSource.now).thenReturn(now)
    when(questionRepository.findRecent(now)).thenReturn(questions)
    val mav = controller.showIndex()
    val model = mav.getModel
    model.containsKey("questions") should be(true)
    model.get("questions") should not be (null)
    model.get("questions") should equal(questions)
  }
}

