package com.cyrusinnovation.inquisition.questions

import org.joda.time.DateTime
import com.cyrusinnovation.inquisition.response.Response

trait QuestionRepository {
    def save(question: Question): Question

    def findById(id: String): Option[Question]

    def findRecent(limit: Int = 10): List[Question]

    def findQuestionCount(): Int

    def deleteQuestion(id: String)
}