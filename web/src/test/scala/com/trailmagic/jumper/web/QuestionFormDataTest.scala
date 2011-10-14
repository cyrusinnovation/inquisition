package com.trailmagic.jumper.web

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}

class QuestionFormDataTest extends FunSuite with BeforeAndAfterEach with ShouldMatchers {

  override def beforeEach() {

  }
  override def afterEach() {

  }

  test("When passed a null tag string an empty tag list is returned") {
    val qfd = new QuestionFormData()
    qfd.setTags(null)
    val question = qfd.toQuestion
    question.tags should not be(null)
    question.tags should have length(0)
  }

  test("When passed an empty tag string an empty tag list is returned") {
    val qfd = new QuestionFormData()
    qfd.setTags("")
    val question = qfd.toQuestion
    question.tags should not be(null)
    question.tags should have length(0)
  }

  test("When passed a comma separated tag string it should parsed into a list") {
    val tagList = "a,b,c"
    val qfd = new QuestionFormData()
    qfd.setTags(tagList)
    val question = qfd.toQuestion
    question.tags should not be(null)
    question.tags should have length(3)
    question.tags(0) should equal("a")
    question.tags(1) should equal("b")
    question.tags(2) should equal("c")
  }
}