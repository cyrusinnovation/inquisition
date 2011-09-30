package com.trailmagic.jumper.test

import org.junit.Assert._
import org.openqa.selenium.{WebElement, By, WebDriver}
import scala.collection.JavaConverters._

class WebDriverHelper(driver: WebDriver) {
  def getErrorMessages() : Iterable[String] = {
    //Get the li elements from the ul element from all divs with the .errors class
    val divErrors = driver.findElements(By.cssSelector("div .errors ul > li")).asScala
    divErrors.map((x) => x.getText)
  }

  def submitRegistrationForm() {
    driver.findElement(By.name("email")).submit()
  }

  def fillAndSubmitLoginForm() {
    driver.findElement(By.id("username")).sendKeys("tester");
    val passwordElement = driver.findElement(By.id("password"));
    passwordElement.sendKeys("password");
    passwordElement.submit();
  }

  def login() {
    driver.get(WebConstants.BaseUrl + "/");

    assertEquals(WebConstants.SecureBaseUrl + "/login", driver.getCurrentUrl)

    fillAndSubmitLoginForm()
  }

  def fillRegistrationForm(username: String = "Username" + System.currentTimeMillis(), password: String = "Password",
                           firstName: String = "FirstName", lastName: String = "LastName",
                           email: String = "Email+" + System.currentTimeMillis() + "@example.com") {
    driver.findElement(By.name("username")).sendKeys(username)
    driver.findElement(By.name("password")).sendKeys(password)
    driver.findElement(By.name("firstName")).sendKeys(firstName)
    driver.findElement(By.name("lastName")).sendKeys(lastName)
    driver.findElement(By.name("email")).sendKeys(email)
  }

  def navigateToRegistrationPage() {
    driver.get(WebConstants.NewUserUrl)
  }
}
