package com.cyrusinnovation.inquisition.response.mongodb

import com.cyrusinnovation.inquisition.questions.Question
import com.cyrusinnovation.inquisition.response.{Response, ResponseRepository}
import com.mongodb.casbah.MongoDB
import org.springframework.beans.factory.annotation.Autowired
import com.mongodb.casbah.commons.MongoDBObject
import org.springframework.stereotype.Repository
import com.cyrusinnovation.inquisition.questions.mongodb.MongoQuestionRepository
import org.bson.types.ObjectId
import java.lang.IllegalArgumentException

@Repository
class MongoResponseRepository @Autowired()(db: MongoDB, questionRepository: MongoQuestionRepository) extends ResponseRepository {
    def generateIdIfEmpty(response: Response): Response = {
        val responseId = response.id match {
            case None => {
                Some(new ObjectId().toStringMongod)
            }
            case Some(id: String) => {
                Some(id)
            }
        }

        response.copy(id = responseId)
    }

    def save(questionId: String, response: Response): Response = {
        questionRepository.findById(questionId) match {
            case None => {
                throw new IllegalArgumentException()
            }
            case Some(question: Question) => {
                val updatedResponse = generateIdIfEmpty(response)
                questionRepository.save(question.copy(responses = updatedResponse :: question.responses))
                updatedResponse
            }
        }
    }

    def updateResponse(response: Response): Response = {
        if (response.id.isEmpty) {
            throw new IllegalArgumentException()
        }

        getResponse(response.id.get) match {
            case None => {
                throw new IllegalArgumentException()
            }
            case Some((q, r)) => {
                val responses = q.responses.filter(x => x.id != response.id)
                questionRepository.save(q.copy(responses = response :: responses))
                response
            }
        }
    }

    def deleteResponse(responseId: String) = null

    def getResponse(responseId: String): Option[(Question, Response)] = {
        questionRepository.findResponseQuestion(responseId) match {
            case Some(x: Question) => {
                val response = x.responses
                        .filter(_.id == Some(new ObjectId(responseId)))
                        .headOption match {
                    case None => {
                        return Option.empty
                    }
                    case Some(y: Response) => {
                        y
                    }
                }
                Some((x, response))
            }
            case None => {
                Option.empty
            }
        }
    }
}