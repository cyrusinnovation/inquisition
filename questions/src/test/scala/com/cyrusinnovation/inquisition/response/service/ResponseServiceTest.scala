package com.cyrusinnovation.inquisition.response.service

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.MongoConnection
import com.cyrusinnovation.inquisition.response.mongodb.MongoResponseRepository
import com.cyrusinnovation.inquisition.response.Response
import com.cyrusinnovation.inquisition.questions.Question
import org.joda.time.DateTime
import com.cyrusinnovation.inquisition.questions.mongodb.{MongoTestConstants, MongoQuestionRepository}
import java.security.InvalidParameterException

@RunWith(classOf[JUnitRunner])
class ResponseServiceTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
    val con = MongoConnection()
    val TestDbName = "test_inquisition"
    val db = con(TestDbName)
    val questionRepository = new MongoQuestionRepository(db)
    val repository = new MongoResponseRepository(db, questionRepository)
    val service = new ResponseServiceImpl(repository)

    override def beforeEach() {
        db("questions").remove(MongoDBObject())
    }

    def createSavedQuestion(responses: List[Response] = List[Response]()): Question = {
        val question = new Question(None, "title " + System.nanoTime(), "testUser", "body", List(), responses, "")
        questionRepository.save(question)
    }

    def createSavedResponse(question: Question): Response = {
        val response = new Response(None, title = "title", creatorUsername = "testUser", body = "body")
        service.save(response, question.id.get, "testUser")
    }

    test("can save a new response") {
        val question = createSavedQuestion()
        val savedResponse = createSavedResponse(question)
        val retrievedResponse = repository.getResponse(savedResponse.id.get).get._2
        savedResponse should be(retrievedResponse)
    }

    test("saving a new response throws exception if question id is null") {
        val response = new Response(None, title = "title", creatorUsername = "testUser", body = "body")
        evaluating {
            service.save(response, null, "testUser")
        } should produce[IllegalArgumentException]
    }

    test("saving a new response throws exception if question id does not exist") {
        val response = new Response(None, title = "title", creatorUsername = "testUser", body = "body")
        evaluating {
            service.save(response, MongoTestConstants.DeadObjectIdString, "testUser")
        } should produce[IllegalArgumentException]
    }

    test("saving a new response throws exception if response is null") {
        val response = new Response(None, title = "title", creatorUsername = "testUser", body = "body")
        evaluating {
            service.save(response, MongoTestConstants.DeadObjectIdString, "testUser")
        } should produce[IllegalArgumentException]
    }

    test("saving a new response throws exception if creatorUsername is null") {
        val question = createSavedQuestion()
        val response = new Response(None, title = "title", creatorUsername = "testUser", body = "body")
        evaluating {
            service.save(response, question.id.get, "")
        } should produce[IllegalArgumentException]
    }

    test("saving a new response throws exception if creatorUsername is empty") {
        val question = createSavedQuestion()
        val response = new Response(None, title = "title", creatorUsername = "testUser", body = "body")
        evaluating {
            service.save(response, question.id.get, null)
        } should produce[IllegalArgumentException]
    }

    test("update a response") {
        val question = createSavedQuestion()
        val response = createSavedResponse(question)
        val expectedResponse = response.copy(body = "Updated Body")

        val responseUnderTest = service.update(response.copy(body = "Updated Body"), "testUser")

        responseUnderTest should be(expectedResponse)
    }

    test("only the response creator can upate a response") {
        val question = createSavedQuestion()
        val response = createSavedResponse(question)

        evaluating {
            service.update(response.copy(body = "Updated Body"), "wrongUser")
        } should produce[InvalidParameterException]
    }

    test("can find question by id") {
        val question = createSavedQuestion()
        val response = createSavedResponse(question)

        val retrievedResponse = service.findById(response.id.get)

        retrievedResponse should be(response)
    }

    test("exception thrown when id is null") {
        evaluating {
            service.findById(null)
        } should produce[IllegalArgumentException]
    }

    test("exception thrown when id is empty") {
        evaluating {
            service.findById("")
        } should produce[IllegalArgumentException]
    }

    test("exception thrown when id does not exist") {
       evaluating {
           service.findById(MongoTestConstants.DeadObjectIdString)
       } should produce[IllegalArgumentException]
    }
}