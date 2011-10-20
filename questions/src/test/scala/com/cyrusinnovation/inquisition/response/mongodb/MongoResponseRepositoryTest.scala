package com.cyrusinnovation.inquisition.response.mongodb

import com.cyrusinnovation.inquisition.questions.Question._
import com.cyrusinnovation.inquisition.response.Response
import com.cyrusinnovation.inquisition.questions.Question
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import com.mongodb.casbah.MongoConnection._
import com.mongodb.casbah.MongoConnection
import org.bson.types.ObjectId
import com.cyrusinnovation.inquisition.questions.mongodb.{MongoTestConstants, MongoQuestionRepository}


class MongoResponseRepositoryTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  val con = MongoConnection()
  val TestDbName = "test_inquisition"
  val db = con(TestDbName)
  val questionRepository = new MongoQuestionRepository(db)
  val responseRepository = new MongoResponseRepository(db, questionRepository)

  override def beforeEach() {
    db.dropDatabase()
  }

  def uniqueQuestion(title: String = "How do I use MongoDB?"): Question = {
    Question(id = None, title = title + " " + System.nanoTime(), creatorUsername = "tester", body = "The question body.")
  }

  test("should be able respond to a question with an anwser") {
    val savedQuestion = questionRepository.save(Question(None, "Title", "Tester", "Body"))
    val response = new Response(None, "Title", "Creator", "Body")
    responseRepository.save(savedQuestion.id.get, response)

    val updatedQuestion = questionRepository.findById(savedQuestion.id.get).get
    val savedResponse = updatedQuestion.responses.head;

    savedQuestion.id should be(updatedQuestion.id)
    savedResponse.id should be('defined)
    savedResponse should equal(response.copy(id = savedResponse.id))
  }

  test("should be able respond to a question with an anwser without creating a duplicate question.") {
    val savedQuestion = questionRepository.save(Question(None, "Title", "Tester", "Body"))

    responseRepository.save(savedQuestion.id.get, new Response(None, "Title", "Creator", "Body"))

    questionRepository.findQuestionCount() should equal(1)
  }

  test("Can update a question with an answer") {
    val answer = new Response(None, "Answer", "creator", "bodystring")
    val question: Question = questionRepository.save(uniqueQuestion())

    val savedResponse = responseRepository.save(question.id.get, answer)

    val retrievedQuestion = questionRepository.findById(question.id.get)
    val answers = retrievedQuestion.get.responses

    answers should have length (1)
    answers.head should equal(savedResponse)
  }

  test("Can save a question response with an existing id") {
    val response = new Response(Some(new ObjectId().toStringMongod), "Answer", "creator", "bodystring")
    val question: Question = questionRepository.save(uniqueQuestion())

    val savedResponse = responseRepository.save(question.id.get, response)
    val retrievedQuestion = questionRepository.findById(question.id.get)
    val responses = retrievedQuestion.get.responses

    responses should have length (1)
    savedResponse should equal(response)
  }

  test("Exception is thrown when a response is added to a non-existant question") {
    val response = new Response(Some(new ObjectId().toStringMongod), "Answer", "creator", "bodystring")
    evaluating {
      responseRepository.save(MongoTestConstants.DeadObjectIdString, response)
    } should produce[IllegalArgumentException]
  }
}