package com.trailmagic.jumper.web

import model.QuestionFormData
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class QuestionFormDataTest extends FunSuite with BeforeAndAfterEach with ShouldMatchers {

    override def beforeEach() {

    }

    override def afterEach() {

    }

    test("When passed a null tag string an empty tag list is returned") {
        val qfd = new QuestionFormData()
        qfd.setTags(null)
        val question = qfd.toQuestion("user")
        question.tags should not be (null)
        question.tags should have length (0)
    }

    test("When passed an empty tag string an empty tag list is returned") {
        val qfd = new QuestionFormData()
        qfd.setTags("")
        val question = qfd.toQuestion("user")
        question.tags should not be (null)
        question.tags should have length (0)
    }

    test("When passed a comma separated tag string it should parsed into a list") {
        val tagList = "a,b,c"
        val qfd = new QuestionFormData()
        qfd.setTags(tagList)
        val question = qfd.toQuestion("user")
        question.tags should not be (null)
        question.tags should have length (3)
        question.tags(0) should equal("a")
        question.tags(1) should equal("b")
        question.tags(2) should equal("c")
    }

    test("When passed one tag should parsed into a list") {
        val tagList = "aTag"
        val qfd = new QuestionFormData()
        qfd.setTags(tagList)
        val question = qfd.toQuestion("user")
        question.tags should not be (null)
        question.tags should have length (1)
        question.tags(0) should equal(tagList)
    }

    test("constructor using a question") {
        val tagList = "a,b,c"
        val qfd = new QuestionFormData()
        qfd.setBody("some body")
        qfd.setId("some id")

        qfd.setTags(tagList)
        qfd.setTitle("some title")
        qfd.setClient("Boston Capital")
        val question = qfd.toQuestion("user")

        val questionUnderTest = new QuestionFormData(question)

        questionUnderTest.getBody() should equal(qfd.getBody())
        questionUnderTest.getId() should equal(qfd.getId())
        questionUnderTest.getTags() should equal(qfd.getTags())
        questionUnderTest.getTitle() should equal(qfd.getTitle())
        questionUnderTest.getClient() should equal(qfd.getClient())
    }
}