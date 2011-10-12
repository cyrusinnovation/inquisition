package com.trailmagic.jumper.test

import org.openqa.selenium.firefox.internal.ProfilesIni
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.junit.Assert._
import org.openqa.selenium.{By, WebDriver}
import org.junit.{Ignore, Test, After, Before}

class JumperAcceptanceTest {
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

  }

  @After
  def tearDown() {
    driver.quit();
  }

  def fillFieldById(tagId: String, keys: String) {
    driver.findElement(By.id(tagId)).sendKeys(keys)
  }

  @Test
  def testRegistrationLinkAppearsOnSignInForm() {
    val originalUrl = WebConstants.HomeUrl
    driver.get(originalUrl)
    driver.findElement(By.linkText("Sign in")).click()
    driver.findElement(By.linkText("Register"))
  }

  @Test
  def testLoginRedirectsToOriginalPage() {
    val originalUrl = WebConstants.HomeUrl
    driver.get(originalUrl)
    driver.findElement(By.linkText("Sign in")).click()
    helper.fillAndSubmitLoginForm()
    assertEquals(originalUrl, driver.getCurrentUrl)

    // And make sure the header shows the right user information
    assertEquals("Authenticated user's name in dropdown",
                 DataHelper.TestUser.firstName + " " + DataHelper.TestUser.lastName,
                 driver.findElement(By.cssSelector(".auth-trigger")).getText)
    helper.getSignOutElement
  }

  @Test
  def testLoginFailureStillRedirectsToOriginalPage() {
    val originalUrl = WebConstants.HomeUrl
    driver.get(originalUrl)
    driver.findElement(By.linkText("Sign in")).click()
    val usernameElement = driver.findElement(By.id("username"))
    usernameElement.sendKeys("i don't exist");
    usernameElement.submit();

    helper.clearLoginForm()
    helper.fillAndSubmitLoginForm()
    assertEquals(originalUrl, driver.getCurrentUrl)
    helper.getSignOutElement
  }

  @Test
  def testLogoutReturnsToFrontPage() {
    helper.login()
    helper.logout()

    assertEquals(WebConstants.HomeUrl, driver.getCurrentUrl)
    assertTrue(helper.isTextIsOnScreen("Sign in"))
  }


  def fillFieldByName(name: String, value: String) {
    driver.findElement(By.name(name)).sendKeys(value)
  }

  @Test
  def testCreateNewUserFailsForExistingUser() {
    driver.get(WebConstants.NewUserUrl)
    val user = DataHelper.TestUser
    fillFieldByName("email", user.email)
    fillFieldByName("username", user.username)
    fillFieldByName("firstName", "eh")
    fillFieldByName("lastName", "whatever")
    fillFieldByName("password", "password")
    driver.findElement(By.cssSelector("input[type='submit']")).click()

    assertEquals(WebConstants.NewUserUrl, driver.getCurrentUrl)
    driver.findElement(By.cssSelector(".errors"))
  }

  @Test
  def testProtectedUrlRequiresLogin() {
    driver.get(WebConstants.ProtectedUrl)
    assertTrue(driver.getCurrentUrl.startsWith(WebConstants.LoginUrl))
  }
}
