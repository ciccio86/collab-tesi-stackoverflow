package it.uniba.di.collab.stackexchange.actorsystem.messages

object Messages {
  case object Work

  case class RawQuestion(questionId: String, creationDate: String, title: String, body: String, tags: Array[String], acceptedDate: Option[String], numberOfComments: Int, commentsText: String, successful: Boolean)

  case class FinalDatasetQuestion(postId: String, codeSnippet: Boolean, Weekday: String, gmtHour: String, bodyLength: Int, titleLength: Int, url: Int, isTheSameTopicBTitle: Boolean, avgUpperCharsPPost: Double, gratitude: Boolean, nTag: Int, sentimentPositiveScore: Int, sentimentNegativeScore: Int, commentSentimentPositiveScore: Either[Int, String], commentSentimentNegativeScore: Either[Int,String], successful: Boolean)
}

