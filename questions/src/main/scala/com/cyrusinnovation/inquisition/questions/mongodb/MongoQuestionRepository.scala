package com.cyrusinnovation.inquisition.questions.mongodb

import com.cyrusinnovation.inquisition.questions.{Question, QuestionAnswer, QuestionRepository}
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
  val tags = db("tags")
  tags.ensureIndex(MongoDBObject("_id" -> 1))
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
    if (!question.get.creatorUsername.equals(usernameRequestingDelete)) {
      throw new InvalidParameterException("requesting user does not have the rights to delte this question")
    }
    questions.remove(MongoDBObject("_id" -> new ObjectId(id)), WriteConcern.Safe)
  }

  def findQuestionsByTags(tags: List[String]): List[Question] = {
    questions.find("tags" $in tags).map(db2question).toList
  }

  def findMostPopularTags(numberToRetreive: Int): List[(String, Int)] = {
    val mapFunction = """function() {
                           if (!this.tags) {
                             return;
                           }

                           for (index in this.tags) {
                             emit(this.tags[index], 1);
                           }
                         }"""

    val reduceFunction = """function(previous, current) {
                              var count = 0;

                              for (index in current) {
                                  count += current[index];
                              }

                              return count;
                            }"""

    val commandBuilder = MongoDBObject.newBuilder
    commandBuilder += "mapreduce" -> "questions"
    commandBuilder += "map" -> mapFunction
    commandBuilder += "reduce" -> reduceFunction
    commandBuilder += "out" -> "tags"
    db.command(commandBuilder.result())

    val tagList = tags.find().sort(MongoDBObject("value" ->  -1)).limit(numberToRetreive)
    tagList.map(x => (x.getAs[String]("_id").get, x.getAs[Int]("value").get)).toList
  }

  def findTagsByPrefix(tagPrefix: String, limit: Int = 10): List[String] = {
    val mongoBlowsBuilder = MongoDBObject.newBuilder
    val regexString = "^" +tagPrefix + ".*"
    mongoBlowsBuilder += "_id" -> regexString.r

    tags.find(mongoBlowsBuilder.result()).limit(limit).map(x => x.getAs[String]("_id").get).toList

  }

  def deleteTagFromQuestion(questionId: String, tagText: String) {
    val updateQuery = $pull (MongoDBObject("tags"->tagText))
    val query = MongoDBObject("_id" -> new ObjectId(questionId))
    questions.update(query ,updateQuery)
//    questions.find(query)
//             .map(x=>println(x))
  }
}