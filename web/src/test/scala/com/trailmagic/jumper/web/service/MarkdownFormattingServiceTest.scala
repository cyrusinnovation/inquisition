package com.trailmagic.jumper.web.service

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class MarkdownFormattingServiceTest extends FunSuite with ShouldMatchers {
  val formatter = new MarkdownFormattingService

  test("Can format simple markdown string") {
    formatter.toActHtmlBlock("*test*") should equal("<p><em>test</em></p>")
    formatter.toPegHtmlBlock("*test*") should equal("<p><em>test</em></p>")
  }
}