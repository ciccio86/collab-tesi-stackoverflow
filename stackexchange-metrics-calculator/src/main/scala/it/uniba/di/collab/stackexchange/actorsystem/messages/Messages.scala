package it.uniba.di.collab.stackexchange.actorsystem.messages

object Messages {

  case object Start

  case class RawQuestion(questionId: String, creationDate: String, title: String, body: String, tags: String, commentsText: String,
                         successful: String, isTheSameTopicBTitle: String)

  case class FinalDatasetQuestion(postId: String, codeSnippet: String, weekday: String, gmtHour: String, bodyLength: String,
                                  titleLength: String, url: String, isTheSameTopicBTitle: String, avgUpperCharsPPost: String,
                                  gratitude: String, nTag: String, sentimentPositiveScore: String, sentimentNegativeScore: String,
                                  commentSentimentPositiveScore: String, commentSentimentNegativeScore: String, successful: String)

  case class Terminated(result: String)

}

