package com.cyrusinnovation.inquisition.questions.mongodb

import org.bson.types.ObjectId



object MongoTestConstants {
  final val DeadObjectIdString: String = "dead6bb0744e9d3695a7f810"
  final val DeadObjectId: ObjectId  = new ObjectId(DeadObjectIdString)
}
