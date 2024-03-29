package com.cyrusinnovation.inquisition.tags.mongodb

import org.scalatest.matchers.ShouldMatchers
import com.mongodb.casbah.MongoConnection
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import com.cyrusinnovation.inquisition.questions.Question
import com.cyrusinnovation.inquisition.questions.mongodb.MongoQuestionRepository
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mongodb.casbah.commons.MongoDBObject

@RunWith(classOf[JUnitRunner])
class MongoTagsRepositoryTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
    val con = MongoConnection()
    val TestDbName = "test_inquisition"
    val db = con(TestDbName)
    val questionRepository = new MongoQuestionRepository(db)
    val tagRepository = new MongoTagRepository(db)

    override def beforeEach() {
        db("tags").remove(MongoDBObject())
        db("questions").remove(MongoDBObject())
    }

    def uniqueQuestion(title: String = "How do I use MongoDB?"): Question = {
        Question(id = None, title = title + " " + System.nanoTime(), creatorUsername = "tester", body = "The question body.")
    }


    test("find all unqiue question tags") {
        questionRepository.save(uniqueQuestion().copy(tags = List("java", "spring")));
        questionRepository.save(uniqueQuestion().copy(tags = List("scala", "spring")));
        questionRepository.findQuestionCount() should equal(2)
        val uniqueTags = tagRepository.findUniqueTagNamesOrderedByTagName()

        uniqueTags should equal(List("java", "scala", "spring"))
    }

    test("find most popular tags") {
        val correctQuestion = questionRepository.save(uniqueQuestion().copy(tags = List("java", "spring")));
        questionRepository.save(uniqueQuestion().copy(tags = List("scala", "spring")));

        val tags = tagRepository.findMostPopularTags(1)

        tags should have size (1)
        tags.head should equal(("spring" -> 2))
    }


    test("find most popular tags in the right order") {
        questionRepository.save(uniqueQuestion().copy(tags = List("java", "spring")));
        questionRepository.save(uniqueQuestion().copy(tags = List("scala", "spring")));
        questionRepository.save(uniqueQuestion().copy(tags = List("java", "spring")));
        questionRepository.save(uniqueQuestion().copy(tags = List("java", "spring")));

        val tags = tagRepository.findMostPopularTags(3)

        tags should have size (3)
        tags.head should equal(("spring" -> 4))
        tags.get("java").get should equal(3.0)
        tags.get("scala").get should equal(1.0)

    }

    test("find most popular tags with no tags in the system") {
        val tags = tagRepository.findMostPopularTags(1)

        tags should have size (0)

    }

    test("find questions with given tag") {
        val correctQuestion = questionRepository.save(uniqueQuestion().copy(tags = List("java", "spring")));
        questionRepository.save(uniqueQuestion().copy(tags = List("scala", "spring")));
        questionRepository.findQuestionCount() should equal(2)
        val question = tagRepository.findQuestionsByTag("java")

        question.length should be(1)
        question.head should equal(correctQuestion)
    }

    test("can do simple tag search") {
        questionRepository.save(uniqueQuestion().copy(tags = List("java")));
        questionRepository.save(uniqueQuestion().copy(tags = List("scala")));
        questionRepository.findQuestionCount() should equal(2)

        val results = tagRepository.findQuestionsByTags(List("java", "scala"))
        results.length should be(2)
    }

    test("simple tag search only returns items with matching tags") {
        val answer = questionRepository.save(uniqueQuestion().copy(tags = List("java")));
        questionRepository.save(uniqueQuestion().copy(tags = List("scala")));
        questionRepository.findQuestionCount() should equal(2)

        val results = tagRepository.findQuestionsByTags(List("java"))
        results.length should be(1)
        results.head should equal(answer)
    }

    test("Can find tags matching a given prefix") {
        val question = uniqueQuestion().copy(tags = List("abc", "def", "ghi", "atag"))
        questionRepository.save(question)
        tagRepository.findMostPopularTags(1)
        val tags = tagRepository.findTagsByPrefix("a", 10)

        tags should have length (2)
        tags.head should equal("abc")
        tags.tail.head should equal("atag")
    }

    test("Can find tags matching a given prefix with a limit") {
        val question = uniqueQuestion().copy(tags = List("abc", "def", "ghi", "atag"))
        questionRepository.save(question)
        tagRepository.findMostPopularTags(1)
        val tags = tagRepository.findTagsByPrefix("a", 1)

        tags should have length (1)
        tags.head should equal("abc")
        tags should not contain ("atag")
    }

    test("Can remove a tag from a given question") {
        val tags = List("abc", "def", "ghi")
        val question = uniqueQuestion().copy(tags = "atag" :: tags)
        val savedQuestion = questionRepository.save(question)

        tagRepository.deleteTagFromQuestion(savedQuestion.id.get, "atag")
        val retrievedQuestion = questionRepository.findById(savedQuestion.id.get)

        retrievedQuestion.get.tags should equal(tags)
    }

    test("No exception is thrown if a non existant tag is removed from a question") {
        val tags = List("abc", "def", "ghi")
        val question = uniqueQuestion().copy(tags = tags)
        val savedQuestion = questionRepository.save(question)

        tagRepository.deleteTagFromQuestion(savedQuestion.id.get, "aNonExistant")
        val retrievedQuestion = questionRepository.findById(savedQuestion.id.get)

        retrievedQuestion.get.tags should equal(tags)
    }

    test("Can add a tag to a given question") {
        val question = uniqueQuestion()
        val savedQuestion = questionRepository.save(question)

        tagRepository.addTagToQuestion(savedQuestion.id.get, "atag")
        val retrievedQuestion = questionRepository.findById(savedQuestion.id.get)

        retrievedQuestion.get.tags should contain("atag")
    }

    test("No exception is thrown if an existant tag is added to a question") {
        val tags = List("abc", "def", "ghi")
        val question = uniqueQuestion().copy(tags = tags)
        val savedQuestion = questionRepository.save(question)

        tagRepository.addTagToQuestion(savedQuestion.id.get, "abc")
        val retrievedQuestion = questionRepository.findById(savedQuestion.id.get)

        retrievedQuestion.get.tags should equal(tags)
    }

    test("No duplicate tag is created if an existant tag is added to a question") {
        val tags = List("abc", "def", "ghi")
        val question = uniqueQuestion().copy(tags = tags)
        val savedQuestion = questionRepository.save(question)

        tagRepository.addTagToQuestion(savedQuestion.id.get, "abc")
        val retrievedQuestion = questionRepository.findById(savedQuestion.id.get)

        retrievedQuestion.get.tags.count(x => x == "abc") should be(1)
    }
}