package com.trailmagic.jumper.test

import org.openqa.selenium.firefox.internal.ProfilesIni
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.junit.Assert._
import org.openqa.selenium.{By, WebDriver}
import org.junit.{Ignore, Test, After, Before}

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

      // eventually be a helper method to cleanup after ourselves
    driver.findElement(By linkText questionTitleText).click()

    driver.findElement(By linkText "Delete Question")

  }


  @Test
  def canDeleteAQuestion() {
    val questionTitleText = "Some Title " + System.currentTimeMillis()
    helper.navigateToQuestionFormAndCreateQuestion(questionTitleText)

      // eventually be a helper method to cleanup after ourselves
    driver.findElement(By linkText questionTitleText).click()

    driver.findElement(By linkText "Delete Question").click()

    assertFalse(helper.isTextIsOnScreen(questionTitleText))

  }
}