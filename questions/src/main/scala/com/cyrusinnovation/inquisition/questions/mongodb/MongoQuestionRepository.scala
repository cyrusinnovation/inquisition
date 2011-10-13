package com.cyrusinnovation.inquisition.questions.mongodb

import com.cyrusinnovation.inquisition.questions.{Question, QuestionAnswer, QuestionRepository}
import org.springframework.beans.factory.annotation.Autowired
import com.novus.salat._
import com.novus.salat.global._
import org.springframework.stereotype.Repository
import org.joda.time.DateTime
import com.mongodb.casbah.Imports._
import com.mongodb.WriteResult
import java.security.InvalidParameterException

@Repository
class MongoQuestionRepository @Autowired()(db: MongoDB) extends QuestionRepository {

  val questions = db("questions")
  questions.ensureIndex(MongoDBObject("tags" -> 1))

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
    val updatedQuestion = question.copy(answers = questionAnswer :: question.answers)
    save(updatedQuestion)
//    Question(None,"Title","BS")
  }

  def db2question(dbObj: DBObject): Question = {
    val question = grater[Question].asObject(dbObj)
    question.copy(id = Some(dbObj("_id").toString))
  }

  def findById(id: String): Option[Question] = {
    val result: Option[DBObject] = questions.findOneByID(new ObjectId(id))
    result.map { db2question }
  }

  def findRecent(now: DateTime): List[Question] = {
    val results = questions.find() map (db2question)
    results.toList
  }

  def findQuestionCount(): Int = {
    questions.count(x => true)
  }

  def findUniqueTagNamesOrderedByTagName(): List[String] = {
    questions.distinct("tags").map(x => x.toString).sortBy(x => x).toList
  }

  def findQuestionsByTag(tag: String): List[Question] = {
    val results = questions.find(MongoDBObject("tags" -> tag))
    results.map(db2question).toList
  }

  def deleteQuestion(id: String, usernameRequestingDelete: String) {
      val question = findById(id)
      if (question == None) {
          return
      }
      if(!question.get.creatorUsername.equals(usernameRequestingDelete)) {
          throw new InvalidParameterException("requesting user does not have the rights to delte this question")
      }
    questions.remove(MongoDBObject("_id" -> new ObjectId(id)), WriteConcern.Safe)
  }

  def findQuestionsByTags(tags: List[String]): List[Question] = {
    questions.find("tags" $in tags).map(db2question).toList
  }

    def findMostPopularTags(numberToRetreive: Int): List[String] = {
       questions.group(MongoDBObject("tags" -> 1), MongoDBObject("sum:0" -> 1), "function(doc, " +
               "prev) { prev.sum += 1}").map(x => x.toString).toList

    }
}