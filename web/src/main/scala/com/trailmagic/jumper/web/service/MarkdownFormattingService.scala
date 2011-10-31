package com.trailmagic.jumper.web.service
import com.tristanhunt.knockoff.DefaultDiscounter._
import org.pegdown.PegDownProcessor
import org.springframework.stereotype.Service

@Service
class MarkdownFormattingService {
  val pegDownProcessor = new PegDownProcessor()

  def toActHtmlBlock(markdown: String): String = {
    toXHTML(knockoff(markdown)).toString()
  }
  def toPegHtmlBlock(markdown: String): String = {
    pegDownProcessor.markdownToHtml(markdown)
  }
}