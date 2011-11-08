package com.cyrusinnovation.inquisition.questions.service

import com.cyrusinnovation.inquisition.questions.{Question, QuestionService}
import org.springframework.beans.factory.annotation.Autowired
import com.cyrusinnovation.inquisition.questions.mongodb.MongoQuestionRepository
import java.security.InvalidParameterException
import org.bson.types.ObjectId
import org.springframework.stereotype.{Service, Component}

@Service
class QuestionServiceImpl @Autowired()(repository: MongoQuestionRepository) extends QuestionService {
    def findQuestionById(questionId: String): Question = {
        repository.findById(questionId) match {
            case None => {
                throw new IllegalArgumentException(questionId + " not found")
            }
            case Some(question: Question) => {
                question
            }
        }
    }

    def findRecent(limit: Int): List[Question] = {
        repository.findRecent(limit)
    }

    def findQuestionCount(): Int = {
        repository.findQuestionCount()
    }

    def deleteQuestion(id: String, usernameRequestingDelete: String) {
        val question = repository.findById(id)
        if (question == None) {
            return
        }
        if (!question.get.creatorUsername.equals(usernameRequestingDelete)) {
            throw new InvalidParameterException("requesting user does not have the rights to delte this question")
        }
        repository.deleteQuestion(id)
    }

    def updateQuestion(question: Question): Question = {
        repository.save(question)
    }

    def createQuestion(question: Question): Question = {
        repository.save(question)
    }
}