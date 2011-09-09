package com.cyrusinnovation.inquisition.questions

trait QuestionRepository {
  def save(question: Question): Question
  def findById(id: String): Option[Question]
}