package com.trailmagic.jumper.test

import org.junit.Assert._
import org.openqa.selenium.{WebElement, By, WebDriver}
import scala.collection.JavaConverters._
import org.openqa.selenium.support.ui.{ExpectedCondition, WebDriverWait}

class WebDriverHelper(driver: WebDriver) {
  def answerQuestion(title: String = "Response Title", body: String = "responseBody", creator: String = "Creator") = {
    driver.findElement(By.linkText("Add Response")).click()

    driver.findElement(By.id("title")).sendKeys(title)
    val questionBodyElement = driver.findElement(By.id("bodyInput"))
    questionBodyElement.sendKeys(title)
    questionBodyElement.submit()
  }

  def tagSearchBoxElement(): WebElement = {
    driver.findElement(By.id("search-form"))
  }

  def logout() {
    getSignOutElement().click()
  }

  def getSignOutElement(): WebElement = {
    //Open drop down menu so the "Sign out" link text is visible and the element can be grabbed
    driver.findElement(By.cssSelector(".auth-trigger")).click()
    driver.findElement(By.linkText("Sign out"))
  }


  def fillAndSubmitQuestionForm(question: String = "this is a question?", questionText: String = "This is the question body.", tagList: String = "TagA,TagB") {
    driver.findElement(By.id("title")).sendKeys(question);
    driver.findElement(By.id("bodyInput")).sendKeys(questionText);
    //        val tagElement = driver.findElement(By.id("tags"))
    //        tagElement.sendKeys(tagList)
    //        tagElement.submit()

    tagList.split(",")
      .map(x => {
      val s = driver.findElement(By.className("tagit-new"))
      val in = s.findElement(By.tagName("input"));
      in.sendKeys(x.trim() + ",")
    });
    driver.findElement(By.id("title")).submit()
  }

  def getErrorMessages(): Iterable[String] = {
    //Get the li elements from the ul element from all divs with the .errors class
    val divErrors = driver.findElements(By.cssSelector("div .errors ul > li")).asScala
    divErrors.map((x) => x.getText)
  }

  def submitRegistrationForm() {
    driver.findElement(By.name("email")).submit()
  }

  def clearLoginForm() {
    driver.findElement(By.id("username")).clear()
    driver.findElement(By.id("password")).clear()
  }

  def fillAndSubmitLoginForm(username: String = "tester", password: String = "password") {
    waitForElementToAppearForAjaxCall("Username")
    driver.findElement(By.id("username")).sendKeys(username);
    val passwordElement = driver.findElement(By.id("password"));
    passwordElement.sendKeys(password);
    passwordElement.submit();
  }

  def login(username: String = "tester", password: String = "password") {
    navigateToLoginPage()

    assertEquals(WebConstants.SecureBaseUrl + "/login", driver.getCurrentUrl)

    fillAndSubmitLoginForm(username, password)
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

  def navigateToLoginPage() {
    driver.get(WebConstants.LoginUrl)
  }

  def navigateToLogoutPage() {
    driver.get(WebConstants.LogoutUrl)

    assertTrue(isTextIsOnScreen("Sign in"))
  }

  def navigateToRegistrationPage() {
    driver.get(WebConstants.NewUserUrl)
  }

  def navigateToQuestionNewForm() {
    driver.get(WebConstants.NewQuestionUrl)
  }

  def isTextIsOnScreen(textToLookFor: String): Boolean = {
    val bodyTag = driver.findElement(By.tagName("body"));
    bodyTag.getText.contains(textToLookFor)

  }

  def waitForElementToAppearForAjaxCall(textToLookFor: String, timeToWaitInSeconds: Int = 2) = {
    new WebDriverWait(driver, timeToWaitInSeconds).until(
      new ExpectedCondition[Boolean] {
        override def apply(d: WebDriver) = isTextIsOnScreen(textToLookFor);
      })
  }

  def navigateToQuestionFormAndCreateQuestion(question: String = "this is a question?", questionText: String = "This is the question body.", tagList: String = "TagA,TagB") {
    navigateToQuestionNewForm()
    assertEquals(WebConstants.NewQuestionUrl, driver.getCurrentUrl)
    fillAndSubmitQuestionForm(question, questionText, tagList)
  }

  def clickDialogConfirmButton {
    val elements = driver.findElements(By className "ui-button-text")
    elements.get(0).click()
  }

  def ensureLoggedOut() {
    try{
      driver.get(WebConstants.LogoutUrl)
      logout()
    }
    catch{
      case e: Exception => {}
    }
  }

  def getDeleteQuestionElement() = {
    driver.findElement(By xpath "//img[@alt='Delete Question']")
  }
}
