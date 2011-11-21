package com.cyrusinnovation.inquisition.questions

import com.cyrusinnovation.inquisition.response.Response

case class Question(id: Option[String], title: String, creatorUsername: String, body: String = "",
                    tags: List[String] = List(), responses: List[Response] = List(), client: String = "") {
}


