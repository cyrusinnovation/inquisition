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
        val retrievedQuestion = service.findQuestionById(savedQuestion.id.get)
        retrievedQuestion.id should be(savedQuestion.id)
    }

    test("should throw illegal argument exception if not found") {
        evaluating {
            service.findQuestionById(MongoTestConstants.DeadObjectIdString)
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
        val updatedQuestion = service.findQuestionById(savedQuestion.id.get)

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


}