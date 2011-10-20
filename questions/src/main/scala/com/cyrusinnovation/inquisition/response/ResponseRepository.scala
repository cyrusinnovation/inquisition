package com.cyrusinnovation.inquisition.response

import com.cyrusinnovation.inquisition.questions.Question


trait ResponseRepository {
         def saveQuestionAnswer(question: Question, response: Response): Question
}