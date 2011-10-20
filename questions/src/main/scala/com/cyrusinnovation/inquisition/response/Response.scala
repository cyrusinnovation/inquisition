package com.cyrusinnovation.inquisition.response

import org.bson.types.ObjectId

case class Response(id: Option[String], title: String, creatorUsername: String, body: String = "") {

}

