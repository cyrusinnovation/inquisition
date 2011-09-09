package com.cyrusinnovation.inquisition.questions.mongodb

import org.scalatest.FunSuite
import org.scalatest.matchers.{ShouldMatchers, MustMatchers}
import com.cyrusinnovation.inquisition.questions.Question
import com.mongodb.casbah.MongoConnection
import org.joda.time.DateTime


class MongoQuestionRepositoryTest extends FunSuite with ShouldMatchers {
  val con = MongoConnection()
  val TestDbName = "test_inquisition"
  val db = con(TestDbName)
  val repository = new MongoQuestionRepository(db)

  test("should be able to save a question and get it back") {
    val q = Question(None, "How do I use MongoDB?", "tester")

    val savedQuestion = repository.save(q)
    savedQuestion.id should be('defined)

    val retrievedQuestion = repository.findById(savedQuestion.id.get).get
    retrievedQuestion should equal(q.copy(id = savedQuestion.id))
  }

  test("should return none if nothing has that id") {
    val result = repository.findById("dead6bb0744e9d3695a7f810")
    result should be(None)
  }

  test("should find recent questions") {
    val questions = List(Question(None, "How do I use MongoDB?", "tester"),
      Question(None, "Why isn't IntelliJ working?", "tester"))

    val savedQuestions = questions.map(repository.save(_))

    val results = repository.findRecent(new DateTime)

    savedQuestions.foreach(results.contains(_) should be (true))
  }
}