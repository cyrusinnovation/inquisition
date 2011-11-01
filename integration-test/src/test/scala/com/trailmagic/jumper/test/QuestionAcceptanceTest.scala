package com.trailmagic.jumper.test

import org.openqa.selenium.firefox.internal.ProfilesIni
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.junit.Assert._
import org.junit.{Ignore, Test, After, Before}
import java.util.concurrent.TimeUnit
import org.openqa.selenium.support.pagefactory.{ElementLocatorFactory, AjaxElementLocatorFactory}
import org.openqa.selenium.{WebElement, By, WebDriver}
import org.openqa.selenium.support.ui.{ExpectedCondition, WebDriverWait}

class QuestionAcceptanceTest {
  //  val capabilities = new DesiredCapabilities("firefox", "3.6.", Platform.WINDOWS)
  //  val driver: WebDriver = new RemoteWebDriver(new URL("http://ostewart:0e34cb73-611e-404f-a7ec-bbcead187fcc@ondemand.saucelabs.com:80/wd/hub"), capabilities)
  //  System.setProperty("webdriver.firefox.profile", "WebDriver")
  var profile: FirefoxProfile = _
  var driver: WebDriver = _
  var helper: WebDriverHelper = _

  implicit def function2function[F, T](f: Function[F, T]): com.google.common.base.Function[F, T] = {
    new com.google.common.base.Function[F, T] {
      def apply(input: F) = {
        f.apply(input)
      }
    }
  }

  @Before
  def setUp() {
    DataHelper.prepareTestData()


    profile = new ProfilesIni().getProfile("WebDriver")
    driver = new FirefoxDriver(profile)
    helper = new WebDriverHelper(driver)
    helper.login()
  }

  @After
  def tearDown() {
    driver.quit();
  }

  @Test
  def canCreateAQuestion() {
    val questionTitleText = "Some Title " + System.currentTimeMillis()
    helper.navigateToQuestionFormAndCreateQuestion(questionTitleText)
    assertEquals(WebConstants.SecureBaseUrl + "/", driver.getCurrentUrl)
    helper.isTextIsOnScreen(questionTitleText)
  }


  @Test
  def canViewAQuestion() {
    val questionTitleText = "Some Title " + System.currentTimeMillis()
    helper.navigateToQuestionFormAndCreateQuestion(questionTitleText)

  }

  @Test
  def optionToDeleteAQuestion() {
    val questionTitleText = "Some Title " + System.currentTimeMillis()
    helper.navigateToQuestionFormAndCreateQuestion(questionTitleText)

    driver.findElement(By linkText questionTitleText).click()

    helper.getDeleteQuestionElement()
  }


  @Test
  def canDeleteAQuestion() {
    val questionTitleText = "Some Title " + System.currentTimeMillis()
    helper.navigateToQuestionFormAndCreateQuestion(questionTitleText)

    driver.findElement(By linkText questionTitleText).click()

    helper.getDeleteQuestionElement().click()

    helper.clickDialogConfirmButton

    assertFalse(helper.isTextIsOnScreen(questionTitleText))

  }

  @Test
  def tagsAreAutoCompleted() {
    helper.navigateToQuestionFormAndCreateQuestion(tagList = "taga, atag, tagb")
    helper.navigateToQuestionNewForm()
    val tagElement = driver.findElement(By.className("tagit-new"))
    val in = tagElement.findElement(By.tagName("input"))
    in.sendKeys("tag")

    helper.waitForElementToAppearForAjaxCall("taga")
    assertTrue(helper.isTextIsOnScreen("tagb"))
  }


  @Test
  def canAnswerAQuestion() {
    val questionTitleText = "Some Title " + System.currentTimeMillis()

    helper.navigateToQuestionFormAndCreateQuestion(questionTitleText)
    val questionUrl = driver.findElement(By linkText questionTitleText).getAttribute("href")
    driver.get(questionUrl)
    helper.answerQuestion(title = "Test Response")
    driver.get(questionUrl)

    assertTrue(helper.isTextIsOnScreen("Test Response"))
  }
}