package com.cyrusinnovation.inquisition.questions

case class Question(id: Option[String], title: String, creatorUsername: String, body: String = "") {
}

case class QuestionAnswer(title: String, creatorUsername: String, body: String = "") {
}