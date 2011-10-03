package com.trailmagic.jumper.web.users

import com.trailmagic.jumper.web.ResourceNotFoundException
import org.junit.Assert._
import org.junit.{Before, Test}
import org.mockito.{MockitoAnnotations, Mock}
import org.springframework.validation.{ObjectError, BindingResult}
import scala.collection.JavaConverters._;
import org.mockito.Mockito.when
import com.trailmagic.jumper.core.{SavedUser, InMemoryUserRepository, User, UserRepository}
import com.trailmagic.jumper.core.security.{NonUniqueEmailException, NonUniqueUsernameException, UserService}

class UsersControllerTest {
  var controller: UsersController = _
  var userRepository: UserRepository = _
  @Mock var userService: UserService = _
  @Mock var bindingData: BindingResult = _
  val TestUser = User("testy@example.com", "tester", "Testy", "McTesterton", "password", "salt")

  @Before
  def setUp() {
    MockitoAnnotations.initMocks(this)
    userRepository = new InMemoryUserRepository
    controller = new UsersController(userRepository, userService)
  }


  @Test
  def testCreateNewUserUser_with_errors() {

    val userFormData = new UserFormData();
    userFormData.setEmail("a@example.com");
    userFormData.setFirstName("Joe");

    val passwordError = new ObjectError("password", "password too short");

    val errors = List(passwordError).asJava;

    when(bindingData.getAllErrors).thenReturn(errors);

    val mav = controller.createNewUser(userFormData, bindingData)
    assertNotNull("should have the user object in the model", mav.getModel.get("user"))
    assertEquals(userFormData, mav.getModel.get("user"))
    assertNotNull("should have the errors object in the model", mav.getModel.get("errors"))
    assertEquals("new-user-form", mav.getViewName)
  }

  @Test
  def testCreateNewUserUser_with_no_errors() {

    val userFormData = new UserFormData();
    userFormData.setEmail("a@example.com");
    userFormData.setFirstName("Joe");



    val errors = List[ObjectError]().asJava;

    when(bindingData.getAllErrors).thenReturn(errors);
    when(userService.createUser(userFormData.toUser)).thenReturn(new SavedUser("id", TestUser))

    val mav = controller.createNewUser(userFormData, bindingData)
    assertNotNull("should have the user object in the model", mav.getModel.get("user"))
    assertNull("should not have the errors object in the model", mav.getModel.get("errors"))
    assertEquals("redirect:signup-thankyou", mav.getViewName)
  }


  @Test
  def testCreateNewUserUser_with_no_errors_and_username_already_used() {

    val userFormData = new UserFormData();
    userFormData.setEmail("a@example.com");
    userFormData.setFirstName("Joe");



    val errors = List[ObjectError]().asJava;

    when(bindingData.getAllErrors).thenReturn(errors);
    when(userService.createUser(userFormData.toUser)).thenThrow(new NonUniqueUsernameException("this is a message"))

    val mav = controller.createNewUser(userFormData, bindingData)
    assertNotNull("should have the user object in the model", mav.getModel.get("user"))
    assertEquals(userFormData, mav.getModel.get("user"))
    assertNotNull("should have the errors object in the model", mav.getModel.get("errors"))
    assertEquals("new-user-form", mav.getViewName)
  }

  @Test
  def testCreateNewUserUser_with_no_errors_and_email_already_used() {

    val userFormData = new UserFormData();
    userFormData.setEmail("a@example.com");
    userFormData.setFirstName("Joe");



    val errors = List[ObjectError]().asJava;

    when(bindingData.getAllErrors).thenReturn(errors);
    when(userService.createUser(userFormData.toUser)).thenThrow(new NonUniqueEmailException("this is a message"))

    val mav = controller.createNewUser(userFormData, bindingData)
    assertNotNull("should have the user object in the model", mav.getModel.get("user"))
    assertEquals(userFormData, mav.getModel.get("user"))
    assertNotNull("should have the errors object in the model", mav.getModel.get("errors"))
    assertEquals("new-user-form", mav.getViewName)
  }




  @Test
  def testUserProfilePage() {
    val savedUser = userRepository.save(TestUser)

    val mav = controller.displayUserProfile(TestUser.username)

    assertEquals(savedUser, mav.getModel.get("user"))
  }

  @Test(expected = classOf[ResourceNotFoundException])
  def testUserProfilePageThrowsExceptionWhenUserNotFound() {
    controller.displayUserProfile("no user")
  }
}
