package com.cyrusinnovation.inquisition.questions

import org.joda.time.DateTime

trait QuestionRepository {
  def save(question: Question): Question
  def findById(id: String): Option[Question]
  def findRecent(now: DateTime): List[Question]
  def findQuestionCount(): Int
  def saveQuestionAnswer(question: Question, questionAnswer: QuestionAnswer): Question
  def findUniqueTagNamesOrderedByTagName(): List[String]
  def findMostPopularTags(numberToRetreive: Int): List[(String, Int)]
  def findQuestionsByTag(tag: String): List[Question]
  def deleteQuestion(id: String, usernameRequestingDelete: String)
  def findQuestionsByTags(tags: List[String]): List[Question]
  def findTagsByPrefix(tagPrefix: String, limit: Int = 10): List[String]
  def addTagToQuestion(questionId: String, tagText: String)
  def deleteTagFromQuestion(questionId: String, tagText: String)
}