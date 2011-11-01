package com.trailmagic.jumper.web.service

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

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


  test("enter text with no html involved") {
    formatter.encodeHtmlBrackets("abcd") should equal("abcd");
  }

  test("enter text with blank string") {
    formatter.encodeHtmlBrackets("") should equal("");
  }

  test("enter text with null string") {
    formatter.encodeHtmlBrackets(null) should equal("");
  }

  test("enter text with less than html brackets involved") {
    formatter.encodeHtmlBrackets("<abcd") should equal("&lt;abcd");
  }

  test("enter text with greater than html brackets involved") {
    formatter.encodeHtmlBrackets("abcd>") should equal("abcd&gt;");
  }

  test("enter text with html brackets involved") {
    formatter.encodeHtmlBrackets("<abcd>") should equal("&lt;abcd&gt;");
  }

  test("enter text with multiple html brackets involved") {
    formatter.encodeHtmlBrackets("<ab<cd>") should equal("&lt;ab&lt;cd&gt;");
  }
}