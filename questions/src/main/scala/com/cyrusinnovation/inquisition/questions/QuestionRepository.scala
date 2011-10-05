package com.cyrusinnovation.inquisition.questions

import org.joda.time.DateTime

trait QuestionRepository {
  def save(question: Question): Question
  def findById(id: String): Option[Question]
  def findRecent(now: DateTime): List[Question]
  def findQuestionCount(): Int
  def saveQuestionAnswer(question: Question, questionAnswer: QuestionAnswer): Question
  def findUniqueTags(): List[String]
  def findQuestionsByTag(tag: String): List[Question]
}