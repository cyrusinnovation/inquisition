package com.trailmagic.jumper.web.users

import com.trailmagic.jumper.web.ResourceNotFoundException
import org.springframework.ui.ModelMap
import com.trailmagic.jumper.core.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{ModelAttribute, RequestMethod, RequestMapping, PathVariable}
import org.springframework.validation.BindingResult
import javax.validation.Valid
import com.trailmagic.jumper.web.util.FormHelper
import org.springframework.web.servlet.ModelAndView
import com.trailmagic.jumper.core.security.{NonUniqueEmailException, NonUniqueUsernameException, UserService}


@Controller
@RequestMapping(Array("/users"))
class UsersController @Autowired()(userRepository: UserRepository, userService: UserService) {
  @RequestMapping(Array("/new"))
  def showNewUserForm() = {
    new ModelAndView("new-user-form", "errors", Set()).addObject("user", new UserFormData)
  }

  @RequestMapping(value = Array("/new"), method = Array(RequestMethod.POST))
  def createNewUser(@ModelAttribute("user") @Valid userData: UserFormData, bindingResult: BindingResult): ModelAndView = {
    val errors = FormHelper.getAllErrors(bindingResult)
    if (!errors.isEmpty) {
      new ModelAndView("new-user-form", "errors", errors).addObject("user",userData)

    } else {
        try {
          val savedUser = userService.createUser(userData.toUser)
          new ModelAndView("redirect:signup-thankyou", "user", savedUser)
        } catch {
          case e: NonUniqueUsernameException => new ModelAndView("new-user-form", "errors", Set("Username is already taken")).addObject("user",userData)
          case e: NonUniqueEmailException => new ModelAndView("new-user-form", "errors", Set("Email is already taken")).addObject("user",userData)

        }
    }
  }

  @RequestMapping(Array("/signup-thankyou"))
  def displaySignupThankyou(): ModelAndView = {
    new ModelAndView("signup-thankyou")
  }

  @RequestMapping(Array("/{username}"))
  def displayUserProfile(@PathVariable username: String): ModelAndView = {
    userRepository.findByUsername(username) match {
      case Some(user) =>
        val model = new ModelMap()
        model.put("user", user)
        new ModelAndView("user-profile", model)
      case None => throw new ResourceNotFoundException
    }
  }
}