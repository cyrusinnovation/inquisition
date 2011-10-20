package com.cyrusinnovation.inquisition.questions.mongodb

import com.cyrusinnovation.inquisition.questions.{Question, QuestionRepository}
import org.springframework.beans.factory.annotation.Autowired
import com.novus.salat._
import com.novus.salat.global._
import org.springframework.stereotype.Repository
import org.joda.time.DateTime
import com.mongodb.casbah.Imports._
import java.security.InvalidParameterException
import com.mongodb.{QueryBuilder, WriteResult}

@Repository
class MongoQuestionRepository @Autowired()(db: MongoDB) extends QuestionRepository {
  val questions = db("questions")
  questions.ensureIndex(MongoDBObject("tags" -> 1))

  def save(question: Question): Question = {
    question.id match {
      case None => {
        val dbObj = grater[Question].asDBObject(question)
        val result = questions.insert(dbObj, WriteConcern.Safe)
        question.copy(id = Some(dbObj("_id").toString))
      }
      case Some(id: String) => {
        val dbObj = grater[Question].asDBObject(question)
        val res = questions.update(MongoDBObject("_id" -> new ObjectId(id)), dbObj, false, false, WriteConcern.Safe)
        question.copy(id = Some(dbObj("id").toString))
      }
    }
  }

  def db2question(dbObj: DBObject): Question = {
    val question = grater[Question].asObject(dbObj)
    question.copy(id = Some(dbObj("_id").toString))
  }

  def findById(id: String): Option[Question] = {
    val result: Option[DBObject] = questions.findOneByID(new ObjectId(id))
    result.map {
      db2question
    }
  }

  def findRecent(now: DateTime): List[Question] = {
    val results = questions.find() map (db2question)
    results.toList
  }

  def findQuestionCount(): Int = {
    questions.count(x => true)
  }

  def deleteQuestion(id: String, usernameRequestingDelete: String) {
    val question = findById(id)
    if (question == None) {
      return
    }
    if (!question.get.creatorUsername.equals(usernameRequestingDelete)) {
      throw new InvalidParameterException("requesting user does not have the rights to delte this question")
    }
    questions.remove(MongoDBObject("_id" -> new ObjectId(id)), WriteConcern.Safe)
  }


}