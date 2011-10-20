package com.cyrusinnovation.inquisition.response.mongodb

import com.cyrusinnovation.inquisition.questions.Question
import com.cyrusinnovation.inquisition.response.{Response, ResponseRepository}
import com.mongodb.casbah.MongoDB
import org.springframework.beans.factory.annotation.Autowired
import com.mongodb.casbah.commons.MongoDBObject
import org.springframework.stereotype.Repository
import com.cyrusinnovation.inquisition.questions.mongodb.MongoQuestionRepository

@Repository
class MongoResponseRepository @Autowired()(db: MongoDB, questionRepository: MongoQuestionRepository) extends ResponseRepository {
  val questions = db("questions")
  questions.ensureIndex(MongoDBObject("tags" -> 1))

  def saveQuestionAnswer(question: Question, questionAnswer: Response): Question = {
    val updatedQuestion = question.copy(answers = questionAnswer :: question.answers)
    questionRepository.save(updatedQuestion)
  }
}