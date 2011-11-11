package com.cyrusinnovation.inquisition.response

import com.cyrusinnovation.inquisition.questions.Question

trait ResponseService {
    def findResponseQuestion(responseId: String): Question
    def findById(responseId: String): Response
    def save(response: Response, questionId: String, responseCreator: String): Response
    def update(response: Response, usernameRequestingUpdate: String): Response
}