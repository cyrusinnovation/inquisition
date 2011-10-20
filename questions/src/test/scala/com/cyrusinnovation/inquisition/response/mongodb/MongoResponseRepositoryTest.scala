package com.cyrusinnovation.inquisition.response.mongodb

import com.cyrusinnovation.inquisition.questions.Question._
import com.cyrusinnovation.inquisition.response.Response
import com.cyrusinnovation.inquisition.questions.Question
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import com.mongodb.casbah.MongoConnection._
import com.cyrusinnovation.inquisition.questions.mongodb.MongoQuestionRepository
import com.mongodb.casbah.MongoConnection


class MongoResponseRepositoryTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  val con = MongoConnection()
  val TestDbName = "test_inquisition"
  val db = con(TestDbName)
  val questionRepository = new MongoQuestionRepository(db)
  val repository = new MongoResponseRepository(db, questionRepository)

  override def beforeEach() {
    db.dropDatabase()
  }

  def uniqueQuestion(title: String = "How do I use MongoDB?"): Question = {
    Question(id = None, title = title + " " + System.nanoTime(), creatorUsername = "tester", body = "The question body.")
  }

  test("should be able respond to a question with an anwser without creating a duplicate question.") {
    val savedQuestion = questionRepository.save(Question(None, "Title", "Tester", "Body"))

    repository.saveQuestionAnswer(savedQuestion, new Response("Title", "Creator", "Body"))
    val updatedQuestion = questionRepository.findById(savedQuestion.id.get).get
    savedQuestion.id should be(updatedQuestion.id)
  }

    test("Can update a question with an answer") {
        val answer = new Response("Answer", "creator", "bodystring")
        val question: Question = uniqueQuestion()

        val savedQuestion = repository.saveQuestionAnswer(question, answer)
        val retrievedQuestion = questionRepository.findById(savedQuestion.id.get)

        val answers = retrievedQuestion.get.answers
        answers should have length (1)
        answers.head should equal(answer)
    }
}