package com.trailmagic.jumper.test

import org.openqa.selenium.firefox.internal.ProfilesIni
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.junit.Assert._
import org.openqa.selenium.{By, WebDriver}
import org.junit.{Ignore, Test, After, Before}

class RegistrationAcceptanceTest {
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
    helper.navigateToRegistrationPage()
  }

  @After
  def tearDown() {
    driver.quit();
  }

  @Test
  def someTest() {
    assertNotNull(profile)
    assertNotNull(driver)
    assertNotNull(helper)
  }

  @Test
  def pageHasCorrectFormEntries() {
    driver.findElement(By.name("username"))
    driver.findElement(By.name("password"))
    driver.findElement(By.name("firstName"))
    driver.findElement(By.name("lastName"))
    driver.findElement(By.name("email"))
  }

  @Test
  def submitForm() {
    helper.fillRegistrationForm()
    helper.submitRegistrationForm()

    assertTrue(driver.getCurrentUrl == WebConstants.SignUpThankYouUrl)
    val bodyTag = driver.findElement(By.tagName("body"));
    val bodyText: Boolean = bodyTag.getText.contains("Thank You")
    assertTrue("Thank you page not displayed.", bodyText)
  }

  @Test
  def invalidEmailAddressCausesError() {
    helper.fillRegistrationForm(email = "badEmail")
    helper.submitRegistrationForm();

    assertTrue(driver.getCurrentUrl != WebConstants.SignUpThankYouUrl)
    val errorMessages = helper.getErrorMessages
    assertTrue("Expected invalid email error message. Actual error messages: " + errorMessages, errorMessages.exists((x) => x.toLowerCase.contains("valid email")))
  }

  @Test
  def shortPasswordCausesError() {
    helper.fillRegistrationForm(password = "p")
    helper.submitRegistrationForm();

    assertTrue(driver.getCurrentUrl != WebConstants.SignUpThankYouUrl)
    val errorMessages = helper.getErrorMessages
    assertTrue("Expected invalid password error message" + errorMessages, errorMessages.exists((x) => x.toLowerCase.contains("password must be at least")))
  }

  @Test
  def allFormFieldsExceptPasswordRefillOnError() {
    helper.fillRegistrationForm(username = "Username", password = "p", email = "a@example.com")
    helper.submitRegistrationForm();

    assertEquals("Username not refilled on failed registration", "Username", driver.findElement(By.name("username")).getAttribute("value"))
    assertEquals("Email not refilled on failed registration", "a@example.com", driver.findElement(By.name("email")).getAttribute("value"))
    assertEquals("Firstname not refilled on failed registration", "FirstName", driver.findElement(By.name("firstName")).getAttribute("value"))
    assertEquals("Lastname not refilled on failed registration", "LastName", driver.findElement(By.name("lastName")).getAttribute("value"))
    assertEquals("Password filled on failed registration", "", driver.findElement(By.name("password")).getAttribute("value"))
  }

}
