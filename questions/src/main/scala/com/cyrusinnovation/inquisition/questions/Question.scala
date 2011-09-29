package com.cyrusinnovation.inquisition.questions

import com.novus.salat.annotations.raw.{Key, Persist, Ignore}
import collection.LinearSeq

case class Question(id: Option[String], title: String, creatorUsername: String, body: String = "", answers: LinearSeq[QuestionAnswer] = List()) {
}

case class QuestionAnswer(title: String, creatorUsername: String, body: String = "") {
}