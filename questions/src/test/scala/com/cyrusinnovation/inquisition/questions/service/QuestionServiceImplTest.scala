package com.cyrusinnovation.inquisition.questions.service

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import com.cyrusinnovation.inquisition.questions.Question
import com.mongodb.casbah.MongoConnection
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.cyrusinnovation.inquisition.questions.mongodb.{MongoTestConstants, MongoQuestionRepository}
import com.mongodb.casbah.commons.MongoDBObject
import java.security.InvalidParameterException
import java.lang.String
import com.cyrusinnovation.inquisition.response.Response


@RunWith(classOf[JUnitRunner])
class QuestionServiceImplTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
    val con = MongoConnection()
    val TestDbName = "test_inquisition"
    val db = con(TestDbName)
    val repository = new MongoQuestionRepository(db)
    val service = new QuestionServiceImpl(repository)

    override def beforeEach() {
        db("questions").remove(MongoDBObject())
    }

    def uniqueQuestion(title: String = "How do I use MongoDB?"): Question = {
        Question(id = None, title = title + " " + System.nanoTime(), creatorUsername = "tester", body = "The question body.")
    }

    test("should be able to save a question and get it back") {
        val q = uniqueQuestion()
        q.id should be(None)
        val savedQuestion = service.createQuestion(q)
        savedQuestion.id should not be (None)
        savedQuestion.id should be('defined)
        val retrievedQuestion = service.findById(savedQuestion.id.get)
        retrievedQuestion.id should be(savedQuestion.id)
    }

    test("should throw illegal argument exception if not found") {
        evaluating {
            service.findById(MongoTestConstants.DeadObjectIdString)
        } should produce[IllegalArgumentException]

    }

    test("should be able to create a question") {
        val q = uniqueQuestion()
        q.id should be(None)
        val savedQuestion = service.createQuestion(q)
        savedQuestion.id should not be (None)
        savedQuestion.id should be('defined)
        repository.findById(savedQuestion.id.get) should be('defined)

    }

    test("should be able to find recent questions") {
        val questions = List(uniqueQuestion(), uniqueQuestion("Why isn't IntelliJ working?"))

        val savedQuestions = questions.map(repository.save(_))

        val results = service.findRecent()

        savedQuestions.foreach(results.contains(_) should be(true))
    }

    test("should be able to find recent questions without passing time") {
        val questions = List(uniqueQuestion(), uniqueQuestion("Why isn't IntelliJ working?"))

        val savedQuestions = questions.map(repository.save(_))

        val results = service.findRecent()

        savedQuestions.foreach(results.contains(_) should be(true))
    }

    test("should not be able to find all questions") {
        val questions = List(uniqueQuestion(), uniqueQuestion("Why isn't IntelliJ working?"))

        questions.map(repository.save(_))

        val results = service.findRecent(1)

        results.size should be(1)
    }

    test("find question total none") {
        var count = service.findQuestionCount()
        count should equal(0)
    }

    test("find question total with 2 questions in the repo") {
        val questions = List(uniqueQuestion(), uniqueQuestion("Why isn't IntelliJ working?"))

        questions.map(repository.save(_))

        val count = service.findQuestionCount()
        count should equal(2)
    }


    test("delete a question with a given object id") {
        val savedQuestion = repository.save(uniqueQuestion())
        val savedQuestionId = savedQuestion.id.get

        service.deleteQuestion(savedQuestionId, savedQuestion.creatorUsername)

        val deletedQuestion = repository.findById(savedQuestionId)
        deletedQuestion should be(None)
    }

    test("delete a question with a given object id, but not the correct username") {
        val savedQuestion = repository.save(uniqueQuestion())
        val savedQuestionId = savedQuestion.id.get
        evaluating(service.deleteQuestion(savedQuestionId, "invalidusername")) should
                (produce[InvalidParameterException])

    }

    test("deleting a non-existant question does not throw an exception") {
        service.deleteQuestion(MongoTestConstants.DeadObjectIdString, "")
    }


    test("should be able to update a question body") {
        val newBodyText: String = "something else"
        val q = uniqueQuestion()
        q.id should be(None)
        val savedQuestion = service.createQuestion(q)
        service.updateQuestion(savedQuestion.copy(body = newBodyText), savedQuestion.creatorUsername)
        val updatedQuestion = service.findById(savedQuestion.id.get)

        updatedQuestion.id should equal(savedQuestion.id)
        updatedQuestion.body should equal(newBodyText)

    }


    test("should be able to update a question body with illegal user creator") {
        val newBodyText: String = "something else"
        val q = uniqueQuestion()
        q.id should be(None)
        val savedQuestion = service.createQuestion(q)
        evaluating {
            service.updateQuestion(savedQuestion.copy(body = newBodyText), MongoTestConstants.InvalidUserName)
        } should produce[IllegalArgumentException]

    }

    test("Should be able to update a question without removing all responses") {
        val r1: Response = new Response(None, title = "Title 1", creatorUsername = "42", body = "body text 1")
        val r2: Response = new Response(None, title = "Title 2", creatorUsername = "42", body = "body text 2")
        val q = uniqueQuestion().copy(responses = List(r1, r2))
        val savedQuestion = repository.save(q)
        savedQuestion.responses should equal(q.responses)
        val updatedQuestion = new Question(id = q.id, body = q.body, title = "Update Title", creatorUsername = q.creatorUsername)
        service.updateQuestion(updatedQuestion, updatedQuestion.creatorUsername)
        val actualQ = service.findById(savedQuestion.id.get)
        actualQ.responses should equal(savedQuestion.responses)
    }

    test("Should be able to update a question without removing all tags") {
        val tags = List("taga", "tagb", "tagc")
        val q = uniqueQuestion().copy(tags = tags)
        val savedQuestion = repository.save(q)
        savedQuestion.tags should equal(tags)
        val updatedQuestion = new Question(id = q.id, body = q.body, title = "Update Title", creatorUsername = q.creatorUsername)
        service.updateQuestion(updatedQuestion, updatedQuestion.creatorUsername)
        val actualQ = service.findById(savedQuestion.id.get)
        actualQ.tags should equal(savedQuestion.tags)
    }

    test("get a list of clients") {
        val client1 = "Boston Capital"
        val client2 = "NFL"
        val client3 = "Nordstroms"

        val expectedList = List(client2, client3)

        val questions = List(uniqueQuestion().copy(client = client1), uniqueQuestion().copy(client = client2),
            uniqueQuestion().copy(client = client3))

        questions.map(repository.save(_))

        val clientList = service.getClientList("n")
        clientList.corresponds(expectedList){_ == _} should be(true)
        clientList should equal(expectedList)
    }

    test("get a limit of 0 of questions without a response") {
        val questions = List(uniqueQuestion(), uniqueQuestion(), uniqueQuestion())

        val expectedList = questions.map(repository.save(_))
        val retrievedQuestions = service.findQuestionsWithoutResponses(0)
        retrievedQuestions should not be(null)
        retrievedQuestions.isEmpty should be(false)

        retrievedQuestions.corresponds(expectedList){_ == _} should be(true)

    }
}