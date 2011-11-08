package com.trailmagic.jumper.web

import model.PreviewFormData
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import service.MarkdownFormattingService
import util.SecurityHelper
import com.trailmagic.jumper.core.{User, SavedUser}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PreviewControllerTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  var formattingService: MarkdownFormattingService = new MarkdownFormattingService
  var controller: PreviewController = _

  override def beforeEach() {
    controller = new PreviewController(formattingService);
    SecurityHelper.setAuthenticatedUser(Some(new SavedUser("userId", new User("a@example.com", "userName",
      "firstName", "lastName", "password", "salt", Set(), None))))
  }

  override def afterEach() {
    SecurityHelper.setAuthenticatedUser(None)
  }

  test("Can properly generate a preview of markdown text") {
    val previewFormData = new PreviewFormData()
    previewFormData.setMarkupText("*test*")

    val mav = controller.generatePreview(previewFormData)

    mav.getViewName should equal("preview")
    mav.getModel().get("previewText") should equal("<p><em>test</em></p>")
  }

  test("Can properly generate a preview of markdown text with html encoded") {
    val previewFormData = new PreviewFormData()
    previewFormData.setMarkupText("*test<html>*")
    val expectedOutput = "<p><em>test<html></em></p>"

    val mav = controller.generatePreview(previewFormData)

    mav.getViewName should equal("preview")
    mav.getModel().get("previewText") should equal(expectedOutput)
  }
}