package com.cyrusinnovation.inquisition.questions

import org.joda.time.DateTime

trait QuestionService {
    def findById(questionId: String): Question

    def findRecent(limit: Int = 10): List[Question]

    def findQuestionCount(): Int

    def deleteQuestion(id: String, usernameRequestingDelete: String)

    def updateQuestion(question: Question, usernameRequestingDelete: String): Question

    def createQuestion(question: Question): Question

    def getClientList(startsWith: String = "", limit: Int = 10): List[String]
}