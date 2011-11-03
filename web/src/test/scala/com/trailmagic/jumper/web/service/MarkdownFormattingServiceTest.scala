package com.trailmagic.jumper.web.service

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class MarkdownFormattingServiceTest extends FunSuite with ShouldMatchers {
  val formatter = new MarkdownFormattingService

  test("Can format simple markdown string") {
    formatter.formatMarkdownAsHtmlBlock("*test*") should equal("<p><em>test</em></p>")
  }

  test("can format an empty String") {
    formatter.formatMarkdownAsHtmlBlock("") should equal("")

  }

  test("can format non-marked up String") {
    formatter.formatMarkdownAsHtmlBlock("abc") should equal("<p>abc</p>")

  }

  test("can format a line return String") {
    formatter.formatMarkdownAsHtmlBlock("abc\ndef") should equal("<p>abc\ndef</p>")

  }

  test("can format a tab in the string") {
    formatter.formatMarkdownAsHtmlBlock("abc\tdef") should equal("<p>abc\tdef</p>")

  }

  test("can format a null String") {
    formatter.formatMarkdownAsHtmlBlock(null) should equal("")

  }

  test("can format html markup") {
    formatter.formatMarkdownAsHtmlBlock("&lt;html&gt;test") should equal("<p>&lt;html&gt;test</p>")
  }

  test("can format unicode text") {
    //unicode text translation: windows\nmouse
    formatter.formatMarkdownAsHtmlBlock("运用视窗\n及滑鼠") should equal("<p>运用视窗\n及滑鼠</p>")
  }


}