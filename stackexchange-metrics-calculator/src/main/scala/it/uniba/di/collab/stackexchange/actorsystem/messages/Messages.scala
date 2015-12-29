package it.uniba.di.collab.stackexchange.actorsystem.messages

object Messages {

  case object Start

  case class RawQuestion(id: String, body: String, `type`: String)

  case class FinalDatasetQuestion(id: String, `type`: String, cleanedBody: String, sentimentPositiveScore: String, sentimentNegativeScore: String)

  case class Terminated(result: String)

  case object QuestionRead

  case object LastQuestionRead

  case object QuestionWritten

  case object QuestionProcessed

}

