package com.trailmagic.jumper.test

import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.internal.ProfilesIni
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.junit.{Ignore, Test, After, Before}
import org.junit.Assert._

/**
 * Created by IntelliJ IDEA.
 * User: Nicholas
 * Date: 10/7/11
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */

class TagSearchAcceptanceTest {
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
  def searchBoxOnlyDisplayedWhenLoggedIn()
  {
    helper.tagSearchBoxElement
  }

  @Test(expected = classOf[org.openqa.selenium.NoSuchElementException])
  def searchBoxNotDisplayedWhenLoggedOut()
  {
    helper.logout()
    helper.tagSearchBoxElement
  }

  @Test
  def canSearchForTags()
  {
    val tagSearch = helper.tagSearchBoxElement
    tagSearch.sendKeys("tag1 tag2");
    tagSearch.submit()
    assertTrue(helper.isTextIsOnScreen("Search Results"))
  }
}