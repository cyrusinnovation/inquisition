package com.cyrusinnovation.inquisition.questions.mongodb

import com.cyrusinnovation.inquisition.questions.{Question, QuestionRepository}
import org.springframework.beans.factory.annotation.Autowired
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import org.springframework.stereotype.Repository
import org.joda.time.DateTime

@Repository
class MongoQuestionRepository @Autowired()(db: MongoDB) extends QuestionRepository {
  val questions = db("questions")

  def save(question: Question): Question = {
    val dbObj = grater[Question].asDBObject(question)
    val result = questions.insert(dbObj, WriteConcern.Safe)

    question.copy(id = Some(dbObj("_id").toString))
  }

  def db2question(dbObj: DBObject): Question = {
      val question = grater[Question].asObject(dbObj)
      question.copy(id = Some(dbObj("_id").toString))
  }

  def findById(id: String): Option[Question] = {
    val result: Option[DBObject] = questions.findOne(MongoDBObject("_id" -> new ObjectId(id)))
    result.map { db2question }
  }

  def findRecent(now: DateTime): List[Question] = {
    val results = questions.find() map (db2question)
    results.toList
  }
}