package com.cyrusinnovation.inquisition.questions.mongodb

import org.scalatest.matchers.ShouldMatchers
import com.mongodb.casbah.MongoConnection
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import com.cyrusinnovation.inquisition.questions.{QuestionAnswer, Question}
import java.security.InvalidParameterException

class MongoQuestionRepositoryTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
    val con = MongoConnection()
    val TestDbName = "test_inquisition"
    val db = con(TestDbName)
    val repository = new MongoQuestionRepository(db)

    override def beforeEach() {
        db.dropDatabase()
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

    test("should be able respond to a question with an anwser without creating a duplicate question.") {
        val savedQuestion = repository.save(Question(None, "Title", "Tester", "Body"))

        repository.saveQuestionAnswer(savedQuestion, new QuestionAnswer("Title", "Creator", "Body"))
        val updatedQuestion = repository.findById(savedQuestion.id.get).get
        savedQuestion.id should be(updatedQuestion.id)
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

        val results = repository.findRecent(new DateTime)

        savedQuestions.foreach(results.contains(_) should be(true))
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

        repository.deleteQuestion(savedQuestionId, savedQuestion.creatorUsername)

        val deletedQuestion = repository.findById(savedQuestionId)
        deletedQuestion should be(None)
    }

    test("delete a question with a given object id, but not the correct username") {
        val savedQuestion = repository.save(uniqueQuestion())
        val savedQuestionId = savedQuestion.id.get
        evaluating(repository.deleteQuestion(savedQuestionId, "invalidusername")) should (produce[InvalidParameterException])

    }

    test("deleting a non-existant question does not throw an exception") {
        repository.deleteQuestion(MongoTestConstants.DeadObjectIdString, "")
    }


    test("Can save a question with an answer") {
        val answer = new QuestionAnswer("Answer", "creator", "body string")
        val question: Question = uniqueQuestion().copy(answers = List(answer))

        val savedQuestion = repository.save(question)
        val retrievedQuestion = repository.findById(savedQuestion.id.get)

        val answers = retrievedQuestion.get.answers
        answers should have length (1)
        answers.head should equal(answer)
    }

    test("Can update a question with an answer") {
        val answer = new QuestionAnswer("Answer", "creator", "bodystring")
        val question: Question = uniqueQuestion()

        val savedQuestion = repository.saveQuestionAnswer(question, answer)
        val retrievedQuestion = repository.findById(savedQuestion.id.get)

        val answers = retrievedQuestion.get.answers
        answers should have length (1)
        answers.head should equal(answer)

    }


}