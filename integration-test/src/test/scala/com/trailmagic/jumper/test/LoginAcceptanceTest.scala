package com.trailmagic.jumper.test

import org.openqa.selenium.firefox.internal.ProfilesIni
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.junit.Assert._
import org.openqa.selenium.{By, WebDriver}
import org.junit.{Test, After, Before}

class LoginAcceptanceTest {

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
    helper.navigateToLoginPage()
  }

  @After
  def tearDown() {
    helper.navigateToLogoutPage();
    driver.quit();
  }

  @Test
  def canaryTest() {
    helper.fillAndSubmitLoginForm()
    assertNotNull(profile)
    assertNotNull(driver)
    assertNotNull(helper)
  }

  @Test
  def pageHasCorrectFormEntries() {
    driver.findElement(By.name("j_username"))
    driver.findElement(By.name("j_password"))
  }

  @Test
  def submitForm() {
    helper.fillAndSubmitLoginForm()
    assertTrue(helper.isTextIsOnScreen(DataHelper.TestUser.firstName))
    assertFalse(helper.isTextIsOnScreen("Login Failed!"))
  }

  @Test
  def invalidPasswordCausesError() {
    helper.fillAndSubmitLoginForm(password = "invalidPassword")
    assertTrue(helper.isTextIsOnScreen("Login Failed!"))
  }

  @Test
  def allFormFieldsExceptPasswordRefillOnError() {
    helper.fillAndSubmitLoginForm(username = "Username", password = "invalidPassword")
    assertEquals("Username not refilled on failed login", "Username", driver.findElement(By.name("j_username"))
            .getAttribute("value"))
  }

}
