package com.cyrusinnovation.inquisition.questions

case class Question(id: Option[String], title: String, creatorUsername: String, body: String = "",
                    tags: List[String] = List(), answers: List[QuestionAnswer] = List()) {
}

case class QuestionAnswer(title: String, creatorUsername: String, body: String = "") {
}