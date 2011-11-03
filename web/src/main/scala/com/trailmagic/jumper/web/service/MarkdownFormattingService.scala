package com.trailmagic.jumper.web.service
import com.tristanhunt.knockoff.DefaultDiscounter._
import org.springframework.stereotype.Service
import java.lang.String

@Service
class MarkdownFormattingService {
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