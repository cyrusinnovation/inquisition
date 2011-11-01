package com.trailmagic.jumper.web.service
import com.tristanhunt.knockoff.DefaultDiscounter._
import org.springframework.stereotype.Service
import java.lang.String

@Service
class MarkdownFormattingService {
  def encodeHtmlBrackets(input: String): String = {
    Option(input) match {
      case None => {
        return ""
      }
      case Some(s) if s.isEmpty => ""
      case Some(s) => {
       s.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
      }
    }
  }

  def formatMarkdownAsHtmlBlock(markdown: String): String = {
    Option(markdown) match {
      case None => {
        return ""
      }
      case Some(s) if s.isEmpty => ""
      case Some(s) => {
       toXHTML(knockoff(s)).toString()
      }

    }
  }
}