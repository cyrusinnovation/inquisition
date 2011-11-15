package com.cyrusinnovation.inquisition.tags.mongodb

import com.cyrusinnovation.inquisition.tags.TagRepository
import org.springframework.beans.factory.annotation.Autowired
import com.novus.salat._
import com.novus.salat.global._
import org.springframework.stereotype.Repository
import com.mongodb.casbah.Imports._
import com.cyrusinnovation.inquisition.questions.Question
import collection.immutable.{ListMap, TreeMap}


@Repository
class MongoTagRepository @Autowired()(db: MongoDB) extends TagRepository {
    val questions = db("questions")
    val tags = db("tags")
    tags.ensureIndex(MongoDBObject("_id" -> 1))
    questions.ensureIndex(MongoDBObject("tags" -> 1))

    def db2question(dbObj: DBObject): Question = {
        val question = grater[Question].asObject(dbObj)
        question.copy(id = Some(dbObj("_id").toString))
    }

    def findUniqueTagNamesOrderedByTagName(): List[String] = {
        questions.distinct("tags").map(x => x.toString).sortBy(x => x).toList
    }

    def findQuestionsByTag(tag: String): List[Question] = {
        val results = questions.find(MongoDBObject("tags" -> tag))
        results.map(db2question).toList
    }

    def findQuestionsByTags(tags: List[String]): List[Question] = {
        questions.find("tags" $in tags).map(db2question).toList
    }

    def findMostPopularTags(numberToRetreive: Int): Map[String, Double] = {
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

        val tagList = tags.find().sort(MongoDBObject("value" -> -1)).limit(numberToRetreive)
        val map = tagList map { t => t.getAs[String]("_id").get -> t.getAs[Double]("value").get }

        ListMap(map.toList:_*)

    }

    def findTagsByPrefix(tagPrefix: String, limit: Int = 10): List[String] = {
        val mongoBlowsBuilder = MongoDBObject.newBuilder
        val regexString = "^" + tagPrefix + ".*"
        mongoBlowsBuilder += "_id" -> regexString.r

        tags.find(mongoBlowsBuilder.result()).limit(limit).map(x => x.getAs[String]("_id").get).toList

    }

    def deleteTagFromQuestion(questionId: String, tagText: String) {
        val updateQuery = $pull(MongoDBObject("tags" -> tagText))
        val query = MongoDBObject("_id" -> new ObjectId(questionId))
        questions.update(query, updateQuery)
    }

    def addTagToQuestion(questionId: String, tagText: String) {
        val updateQuery = $addToSet("tags" -> tagText)
        val query = MongoDBObject("_id" -> new ObjectId(questionId))
        questions.update(query, updateQuery)
    }
}