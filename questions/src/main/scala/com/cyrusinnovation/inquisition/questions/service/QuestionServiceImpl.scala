package com.cyrusinnovation.inquisition.questions.service

import com.cyrusinnovation.inquisition.questions.{Question, QuestionService}
import org.springframework.beans.factory.annotation.Autowired
import com.cyrusinnovation.inquisition.questions.mongodb.MongoQuestionRepository
import java.security.InvalidParameterException
import org.springframework.stereotype.Service


@Service
class QuestionServiceImpl @Autowired()(repository: MongoQuestionRepository) extends QuestionService {
    def findById(questionId: String): Question = {
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
        val question = repository.findById(id) match {
            case None => return
            case Some(q) => q
        }

        verifyUserHasRights("delete", question.creatorUsername, usernameRequestingDelete)
        repository.deleteQuestion(id)
    }

    def updateQuestion(question: Question, usernameRequestingDelete: String): Question = {
        verifyUserHasRights("update", question.creatorUsername, usernameRequestingDelete)
        repository.save(question)
    }

    def verifyUserHasRights(method: String, questionUserName: String, requestingUserName: String) {
        method match {

            case "delete" => {
                if (!questionUserName.equals(requestingUserName)) {
                    throw new InvalidParameterException("requesting user does not have the rights to delete this question")
                }

            }
            case "update" => {
                if (!questionUserName.equals(requestingUserName)) {
                    throw new InvalidParameterException("requesting user does not have the rights to update this question")
                }

            }
            case _ => {
                throw new InvalidParameterException("cannot verify what type of validation you're trying to do here")
            }
        }

    }

    def createQuestion(question: Question): Question = {
        repository.save(question)
    }

    def getClientList(startsWith: String, limit: Int): List[String] = {
        repository.getClientList(startsWith, limit)
    }

    def findQuestionsWithoutResponses(limit: Int = 10): List[Question] = {
        repository.findQuestionsWithoutResponses(limit)
    }
}