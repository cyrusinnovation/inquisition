package com.cyrusinnovation.inquisition.questions.mongodb

import com.cyrusinnovation.inquisition.questions.{Question, QuestionRepository}
import org.springframework.beans.factory.annotation.Autowired
import com.novus.salat._
import com.novus.salat.global._
import org.springframework.stereotype.Repository
import com.mongodb.casbah.Imports._


@Repository
class MongoQuestionRepository @Autowired()(db: MongoDB) extends QuestionRepository {


    val questions = db("questions")
    questions.ensureIndex(MongoDBObject("responses.id" -> 1))
    questions.ensureIndex(MongoDBObject("tags.name" -> 1))

    def save(question: Question): Question = {
        question.id match {
            case None => {
                val dbObj = grater[Question].asDBObject(question)
                questions.insert(dbObj, WriteConcern.Safe)
                question.copy(id = Some(dbObj("_id").toString))
            }
            case Some(id: String) => {
                val dbObj = grater[Question].asDBObject(question)
                questions.update(MongoDBObject("_id" -> new ObjectId(id)), dbObj, false, false, WriteConcern.Safe)
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

    def findRecent(limit: Int): List[Question] = {
        val results = questions.find().limit(limit) map (db2question)
        results.toList
    }

    def findQuestionCount(): Int = {
        questions.count(x => true)
    }

    def deleteQuestion(id: String) {
        questions.remove(MongoDBObject("_id" -> new ObjectId(id)), WriteConcern.Safe)
    }


    def getClientList(startsWith: String, limit: Int): List[String] = {
        val clients = questions.distinct("client")
                 .map(x => x.toString)
                 .filter(_.toLowerCase.startsWith(startsWith.toLowerCase))
                 .sortBy(x => x).toList
        if (limit <= 0) {
            return clients
        }
        clients.take(limit)
    }

    def findResponseQuestion(responseId: String): Option[Question] = {
        questions.findOne(MongoDBObject("responses.id" -> responseId))
                .map(db2question(_))
                .toList
                .headOption
    }

    def findQuestionsWithoutResponses(limit: Int): List[Question] = {
        questions.find("responses" $size 0).limit(limit).map(db2question).toList
    }
}