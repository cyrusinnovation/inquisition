package com.trailmagic.jumper.web.users

import com.trailmagic.jumper.web.ResourceNotFoundException
import com.trailmagic.jumper.core.security.UserService
import org.junit.Assert._
import org.junit.{Before, Test}
import org.mockito.{MockitoAnnotations, Mock}
import com.trailmagic.jumper.core.{InMemoryUserRepository, User, UserRepository}
import org.springframework.validation.{ObjectError, BindingResult}
import scala.collection.JavaConverters._;
import org.mockito.Mockito.when




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
    assertTrue(mav.getModel.get("user") != null)
    assertEquals(userFormData, mav.getModel.get("user"))
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
