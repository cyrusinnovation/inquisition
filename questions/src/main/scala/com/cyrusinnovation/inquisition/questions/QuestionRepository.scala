package com.cyrusinnovation.inquisition.questions

trait QuestionRepository {
    def save(question: Question): Question

    def findById(id: String): Option[Question]

    def findRecent(limit: Int = 10): List[Question]

    def findQuestionCount(): Int

    def deleteQuestion(id: String)

    def getClientList(startsWith: String = "", limit: Int = 10): List[String]
}