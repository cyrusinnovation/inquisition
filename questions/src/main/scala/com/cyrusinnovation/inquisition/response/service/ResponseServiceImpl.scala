package com.cyrusinnovation.inquisition.response.service

import com.cyrusinnovation.inquisition.response.mongodb.MongoResponseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.String
import java.security.InvalidParameterException
import com.cyrusinnovation.inquisition.response.{ResponseRepository, ResponseService, Response}
import com.cyrusinnovation.inquisition.questions.Question

@Service
class ResponseServiceImpl @Autowired()(repository: ResponseRepository) extends ResponseService {
    def findById(responseId: String): Response = {
            repository.getResponse(responseId) match {
            case None => {
                throw new IllegalArgumentException(responseId + " not found")
            }
            case Some((question: Question, response: Response)) => {
                response
            }
        }
    }

    def verifyUserCreatedResponse(response: Response, requestingUsername: String) {
        val retrievedResponse = repository.getResponse(response.id.get)
        if (retrievedResponse.get._2.creatorUsername != requestingUsername)
        {
            throw new InvalidParameterException("requesting user does not have the rights to update this question")
        }
    }

    def update(response: Response, usernameRequestingUpdate: String): Response = {
        verifyUserCreatedResponse(response, usernameRequestingUpdate)
        repository.updateResponse(response)
    }

    def save(response: Response, questionId: String, responseCreator: String): Response = {
        if (questionId == null || questionId.isEmpty) {
            throw new IllegalArgumentException("questionId was null or empty.")
        }
        if (response == null) {
            throw new IllegalArgumentException("response was null.")
        }
        if (responseCreator == null || responseCreator.isEmpty) {
            throw new IllegalArgumentException("responseCreator was null or empty.")
        }
        val userResponse = response.copy(creatorUsername = responseCreator)
        repository.save(questionId, userResponse)
    }

    def findResponseQuestion(responseId: String): Question = {
        repository.getResponse(responseId).get._1
    }
}