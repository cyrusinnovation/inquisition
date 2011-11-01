package com.trailmagic.jumper.test

import org.openqa.selenium.firefox.internal.ProfilesIni
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.junit.Assert._
import org.openqa.selenium.{By, WebDriver}
import org.junit.{Ignore, Test, After, Before}

class HomePageAcceptanceTest {
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
  def homePageShowsTags() {
    val tagOne = "tagOne, tagTwo, tagThree, tagFour, tagFive, tagSix, tagSeven, tagEight, tagNine, tagTen, TagEleven"
    val tagTwo = "tagOne, tagTwo, tagThree, tagFour, tagFive, tagSix, tagSeven, tagEight, tagNine, tagTen"

    helper.navigateToQuestionFormAndCreateQuestion(tagList = tagOne)
    helper.navigateToQuestionFormAndCreateQuestion(tagList = tagTwo)

    tagTwo.split(", ").map(x => assertTrue("Tag should be on screen: " + x, helper.isTextIsOnScreen(x)))
    assertFalse(helper.isTextIsOnScreen("tagEleven"))
  }

  @Test
  def verifyNoTagsDisplayWhenNoneWereEntered() {

    helper.navigateToQuestionFormAndCreateQuestion(tagList = "")

    val element = driver.findElement(By id "questionTags")
    assertTrue(element.getText, element.getText.isEmpty)
  }
}