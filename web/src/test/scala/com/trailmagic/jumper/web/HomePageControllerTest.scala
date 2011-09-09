package com.trailmagic.jumper.web

import org.joda.time.DateTime
import org.mockito.Mockito
import com.trailmagic.jumper.core.signup.SignupRepository
import com.trailmagic.jumper.core.TimeSource
import org.junit.Test
import org.junit.Assert._
import com.trailmagic.jumper.core.signup.SignupRequest
import com.cyrusinnovation.inquisition.questions.QuestionRepository


class HomePageControllerTest {
  val TheTime = new DateTime
  val repository = Mockito.mock(classOf[QuestionRepository])
  val controller = new HomePageController(repository, new TimeSource {
    override def now = TheTime
  })

}