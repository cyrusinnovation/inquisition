package com.cyrusinnovation.inquisition.response

import com.cyrusinnovation.inquisition.questions.Question

trait ResponseRepository {
  def save(questionId: String, response: Response): Response
  def updateResponse(response: Response): Response
  def deleteResponse(responseId: String)
  def getResponse(responseId: String): Option[(Question, Response)]
}