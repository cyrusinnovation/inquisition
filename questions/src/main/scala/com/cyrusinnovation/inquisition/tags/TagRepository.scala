package com.cyrusinnovation.inquisition.tags

import com.cyrusinnovation.inquisition.questions.Question

trait TagRepository {

    def findQuestionsByTag(tag: String): List[Question]

    def findQuestionsByTags(tags: List[String]): List[Question]

    def findUniqueTagNamesOrderedByTagName(): List[String]

    def findMostPopularTags(numberToRetreive: Int = 10): List[(String, Int)]

    def findTagsByPrefix(tagPrefix: String, limit: Int = 10): List[String]

    def addTagToQuestion(questionId: String, tagText: String)

    def deleteTagFromQuestion(questionId: String, tagText: String)

}