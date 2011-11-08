package com.cyrusinnovation.inquisition.questions

import org.joda.time.DateTime

trait QuestionService {
    def findQuestionById(questionId: String): Question

    def findRecent(limit: Int = 10): List[Question]

    def findQuestionCount(): Int

    def deleteQuestion(id: String, usernameRequestingDelete: String)

    def updateQuestion(question: Question): Question

    def createQuestion(question: Question): Question
}