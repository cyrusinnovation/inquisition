package com.cyrusinnovation.inquisition.response

trait ResponseRepository {
  def save(questionId: String, response: Response): Response
  def updateResponse(response: Response): Response
  def deleteResponse(responseId: String)
  def getResponse(responseId: String): Response
}