package com.trailmagic.jumper.web

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.mockito.MockitoAnnotations.Mock
import service.MarkdownFormattingService
import org.mockito.MockitoAnnotations
import util.SecurityHelper
import com.trailmagic.jumper.core.{User, SavedUser}
import org.mockito.Mockito._

class PreviewControllerTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  @Mock var formattingService: MarkdownFormattingService = _
  var controller: PreviewController = _

  override def beforeEach() {
    MockitoAnnotations.initMocks(this)
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

    when(formattingService.toPegHtmlBlock("*test*")).thenReturn("formatted")
    val mav = controller.generatePreview(previewFormData)

    mav.getViewName should equal("preview")
    mav.getModel().get("previewText") should equal("formatted")
  }
}