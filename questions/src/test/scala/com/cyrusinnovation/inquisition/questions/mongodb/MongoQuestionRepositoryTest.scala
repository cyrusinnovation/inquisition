package com.cyrusinnovation.inquisition.questions.mongodb

import org.scalatest.matchers.ShouldMatchers
import com.mongodb.casbah.MongoConnection
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import com.cyrusinnovation.inquisition.response.Response
import com.cyrusinnovation.inquisition.questions.Question
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mongodb.casbah.commons.MongoDBObject
import java.lang.String
import org.bson.types.ObjectId

@RunWith(classOf[JUnitRunner])
class MongoQuestionRepositoryTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
    val con = MongoConnection()
    val TestDbName = "test_inquisition"
    val db = con(TestDbName)
    val repository = new MongoQuestionRepository(db)

    override def beforeEach() {
        db("questions").remove(MongoDBObject())
    }

    def uniqueQuestion(title: String = "How do I use MongoDB?"): Question = {
        Question(id = None, title = title + " " + System.nanoTime(), creatorUsername = "tester", body = "The question body.")
    }

    test("should be able to save a question and get it back") {
        val q = uniqueQuestion()
        q.id should be(None)
        val savedQuestion = repository.save(q)
        savedQuestion.id should not be (None)
        savedQuestion.id should be('defined)
        val retrievedQuestion = repository.findById(savedQuestion.id.get).get
        retrievedQuestion.id should be(savedQuestion.id)
    }

    test("should be able to count the number of questions") {
        val q = uniqueQuestion()
        repository.save(q)
        repository.save(q)
        repository.save(q)

        val questionCount = repository.findQuestionCount()
        questionCount should be(3)
    }



    test("should be able to save a question with an existing id to update it") {
        val savedQuestion = repository.save(uniqueQuestion(title = "Original Title"));

        val updatedQuestion = repository.save(savedQuestion.copy(title = "Updated Title"))
        val questionCount = repository.findQuestionCount()

        questionCount should be(1)
        updatedQuestion.id should be(savedQuestion.id)
        updatedQuestion.title should not be (savedQuestion.title)

        val updatedQuestionReturned = repository.findById(updatedQuestion.id.get).get
        updatedQuestionReturned.title should be(updatedQuestion.title)
    }


    test("should return none if nothing has that id") {
        val result = repository.findById(MongoTestConstants.DeadObjectIdString)
        result should be(None)
    }

    test("should find recent questions") {
        val questions = List(uniqueQuestion(), uniqueQuestion("Why isn't IntelliJ working?"))

        val savedQuestions = questions.map(repository.save(_))

        val results = repository.findRecent()

        savedQuestions.foreach(results.contains(_) should be(true))
    }

    test("should not be able to find recent questions") {
        val questions = List(uniqueQuestion(), uniqueQuestion("Why isn't IntelliJ working?"))

        questions.map(repository.save(_))

        val results = repository.findRecent(1)

        results.size should be(1)
    }

    test("make sure we can add a list of tags to a question") {
        var question = uniqueQuestion()

        question = question.copy(tags = List("java", "spring"))

        val savedQuestion = repository.save(question);

        question = question.copy(id = savedQuestion.id)

        question should equal(savedQuestion)
    }

    test("make sure we can get a list of tags to a question") {
        var question = uniqueQuestion()

        question = question.copy(tags = List("java", "spring"))

        val savedQuestion = repository.save(question);

        val returnedQuestion = repository.findById(savedQuestion.id.get)

        savedQuestion.tags should equal(returnedQuestion.get.tags)
    }


    test("delete a question with a given object id") {
        val savedQuestion = repository.save(uniqueQuestion())
        val savedQuestionId = savedQuestion.id.get

        repository.deleteQuestion(savedQuestionId)

        val deletedQuestion = repository.findById(savedQuestionId)
        deletedQuestion should be(None)
    }

    test("deleting a non-existant question does not throw an exception") {
        repository.deleteQuestion(MongoTestConstants.DeadObjectIdString)
    }


    test("Can save a question with an answer") {
        val response = new Response(None, "Answer", "creator", "body string")
        val question: Question = uniqueQuestion().copy(responses = List(response))

        val savedQuestion = repository.save(question)
        val retrievedQuestion = repository.findById(savedQuestion.id.get)

        val answers = retrievedQuestion.get.responses
        answers should have length (1)
        answers.head should equal(response)
    }

    test("can get list of all clients") {


        val client1 = "ABC"
        val client2 = "Boston Capital"
        val client3 = "DEF"
        val client4 = "GHI"
        val client5 = "JKL"
        val client6 = "MNO"
        val client7 = "NFL"
        val client8 = "PQR"
        val client9 = "STU"
        val client10 = "VWX"
        val client11 = "YZ"

        val expectedList = List(client1, client2, client3, client4, client5, client6, client7, client8, client9,
            client10, client11)

        for(client <- expectedList) {
            repository.save(uniqueQuestion().copy(client = client))
        }

        val clientList = repository.getClientList(limit = 0)
        clientList should not be (null)
        clientList.isEmpty should be(false)
        clientList.size should be(11)
        clientList.corresponds(expectedList){_ == _} should be(true)
    }



    test("can get list of all clients in the correct order") {
        val client1 = "Boston Capital"
        val client2 = "NFL"
        val client3 = "Akami"

        val questions = List(uniqueQuestion().copy(client = client1), uniqueQuestion().copy(client = client2),
            uniqueQuestion().copy(client = client3))

        questions.map(repository.save(_))

        val clientList = repository.getClientList()
        clientList(0) should equal(client3)
    }

    test("can limit list of clients and in the correct order") {
        val client1 = "Boston Capital"
        val client2 = "NFL"
        val client3 = "Akami"

        val questions = List(uniqueQuestion().copy(client = client1), uniqueQuestion().copy(client = client2),
            uniqueQuestion().copy(client = client3))

        questions.map(repository.save(_))

        val clientList = repository.getClientList(limit = 1)
        clientList.size should be(1)
        clientList(0) should equal(client3)
    }

    test("Do Starts with list of clients") {
        val client1 = "Boston Capital"
        val client2 = "NFL"
        val client3 = "Akami"

        val questions = List(uniqueQuestion().copy(client = client1), uniqueQuestion().copy(client = client2),
            uniqueQuestion().copy(client = client3))

        questions.map(repository.save(_))

        val clientList = repository.getClientList("N")
        clientList.size should be(1)
        clientList(0) should equal(client2)
    }

    test("Do Starts with list of clients with multiple results") {
        val client1 = "Boston Capital"
        val client2 = "NFL"
        val client3 = "Nordstroms"

        val questions = List(uniqueQuestion().copy(client = client1), uniqueQuestion().copy(client = client2),
            uniqueQuestion().copy(client = client3))

        questions.map(repository.save(_))

        val clientList = repository.getClientList("N")
        clientList.size should be(2)
        clientList(0) should equal(client2)
        clientList(1) should equal(client3)
    }

    test("Do Starts with list of clients with multiple results case insenitive") {
        val client1 = "Boston Capital"
        val client2 = "NFL"
        val client3 = "Nordstroms"

        val questions = List(uniqueQuestion().copy(client = client1), uniqueQuestion().copy(client = client2),
            uniqueQuestion().copy(client = client3))

        questions.map(repository.save(_))

        val clientList = repository.getClientList("n")
        clientList.size should be(2)
        clientList(0) should equal(client2)
        clientList(1) should equal(client3)
    }

    test("Can find the question that a response belongs to") {
        val responseId: String = new ObjectId().toStringMongod
        val response = new Response(Some(responseId), "Title", creatorUsername = "tester", body = "body")
        val expectedQuestion = repository.save(uniqueQuestion().copy(responses = List(response)))
        val retrievedQuestion = repository.findResponseQuestion(responseId).get
        retrievedQuestion should equal(expectedQuestion)
    }

    test("No question returned for non-existant response id") {
        val retrievedQuestion = repository.findResponseQuestion(MongoTestConstants.DeadObjectIdString)
        retrievedQuestion should not be('defined)
    }

    test("get a list of questions without responses basic") {

        var question = uniqueQuestion()

        question = question.copy(tags = List("java", "spring"))

        val savedQuestion = repository.save(question);

        val retrievedQuestions = repository.findQuestionsWithoutResponses()
        retrievedQuestions should not be(null)
        retrievedQuestions.isEmpty should be(false)
        retrievedQuestions should contain(savedQuestion)
    }

    test("don't get a question with a response") {
        val response = new Response(id = None, title = "def", body = "abc", creatorUsername = "someUser")

        val questions = List(uniqueQuestion().copy(responses = List(response)))

        questions.map(repository.save(_))
        val retrievedQuestions = repository.findQuestionsWithoutResponses()
        retrievedQuestions should not be(null)
        retrievedQuestions.isEmpty should be(true)

    }

    test("get a list of questions without a response") {
        val questions = List(uniqueQuestion(), uniqueQuestion(), uniqueQuestion())

        val expectedList = questions.map(repository.save(_))
        val retrievedQuestions = repository.findQuestionsWithoutResponses()
        retrievedQuestions should not be(null)
        retrievedQuestions.isEmpty should be(false)

        retrievedQuestions.corresponds(expectedList){_ == _} should be(true)

    }

    test("get a limited list of questions without a response") {
        val questions = List(uniqueQuestion(), uniqueQuestion(), uniqueQuestion())

        val expectedList = questions.map(repository.save(_))
        val retrievedQuestions = repository.findQuestionsWithoutResponses(1)
        retrievedQuestions should not be(null)
        retrievedQuestions.isEmpty should be(false)
        retrievedQuestions.size should be(1)


    }

    test("get a limit of 0 of questions without a response") {
        val questions = List(uniqueQuestion(), uniqueQuestion(), uniqueQuestion())

        val expectedList = questions.map(repository.save(_))
        val retrievedQuestions = repository.findQuestionsWithoutResponses(0)
        retrievedQuestions should not be(null)
        retrievedQuestions.isEmpty should be(false)

        retrievedQuestions.corresponds(expectedList){_ == _} should be(true)

    }
}