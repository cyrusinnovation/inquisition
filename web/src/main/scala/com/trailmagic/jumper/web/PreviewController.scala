package com.trailmagic.jumper.web

import model.PreviewFormData
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import service.MarkdownFormattingService
import org.springframework.web.servlet.ModelAndView
import scala.collection.JavaConverters._
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, ModelAttribute}
import org.springframework.web.bind.annotation.RequestMapping._

@Controller
@RequestMapping(value = Array("/preview"))
class PreviewController @Autowired()(formattingService: MarkdownFormattingService) {

  @RequestMapping(value = Array("/generate"), method = Array(RequestMethod.POST))
  def generatePreview(@ModelAttribute previewData: PreviewFormData): ModelAndView = {

    val previewText = formattingService.formatMarkdownAsHtmlBlock(previewData.getMarkupText())
    new ModelAndView("preview", Map("previewText" -> previewText).asJava)
  }

  @RequestMapping(value = Array("/help"))
  def showHelp() = {
    "markdown-help"
  }
}