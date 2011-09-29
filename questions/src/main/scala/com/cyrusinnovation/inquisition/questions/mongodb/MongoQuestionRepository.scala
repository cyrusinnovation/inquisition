package com.cyrusinnovation.inquisition.questions.mongodb

import com.cyrusinnovation.inquisition.questions.{Question, QuestionAnswer, QuestionRepository}
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
    question.id match {
      case None =>{
            val dbObj = grater[Question].asDBObject(question)
            val result = questions.insert(dbObj, WriteConcern.Safe)
            question.copy(id = Some(dbObj("_id").toString))
      }
      case Some(id: String) => {
            val dbObj = grater[Question].asDBObject(question)
            val res = questions.update(MongoDBObject("_id" -> new ObjectId(id)), dbObj, false,false, WriteConcern.Safe)
            question.copy(id = Some(dbObj("id").toString))
      }
    }
  }

  def saveQuestionAnswer(question: Question, questionAnswer: QuestionAnswer): Question = {
      Question(None, "Title", "UserName")
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

  def findQuestionCount(): Int = {
    questions.count(x => true)
  }
}