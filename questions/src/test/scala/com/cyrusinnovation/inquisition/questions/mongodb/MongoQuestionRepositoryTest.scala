package com.cyrusinnovation.inquisition.questions.mongodb

import org.scalatest.FunSuite
import org.scalatest.matchers.{ShouldMatchers, MustMatchers}
import com.cyrusinnovation.inquisition.questions.Question
import com.mongodb.casbah.MongoConnection


class MongoQuestionRepositoryTest extends FunSuite with ShouldMatchers {
  val con = MongoConnection()
  val TestDbName = "test_inquisition"
  val db = con(TestDbName)

  test("should be able to save a question and get it back") {
    val q = Question(None, "How do I use MongoDB?", "tester")

    val repository = new MongoQuestionRepository(db)

    val savedQuestion = repository.save(q)

    savedQuestion.id should be ('defined)
  }
}