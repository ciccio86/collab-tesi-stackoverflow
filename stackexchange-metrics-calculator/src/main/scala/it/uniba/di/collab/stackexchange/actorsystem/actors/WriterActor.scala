package it.uniba.di.collab.stackexchange.actorsystem.actors

import akka.actor.Actor
import com.github.tototoshi.csv.CSVWriter
import it.uniba.di.collab.stackexchange.actorsystem.messages.Messages._

/**
 * Created by francesco on 24/09/15.
 */
class WriterActor(writer: CSVWriter) extends Actor {

  private val master = context.parent


  writer.writeRow(List("PostId", "CodeSnippet", "Weekday", "GMTHour", "BodyLength", "TitleLength", "URL", "IsTheSameTopicBTitle",
    "AvgUpperCharsPPost", "Gratitude", "NTag", "SentimentPositiveScore", "SentimentNegativeScore", "CommentSentimentPositiveScore",
    "CommentSentimentNegativeScore", "Successful"))

  def receive = {
    case finalQuestion: FinalDatasetQuestion =>
      writer.writeRow(List(finalQuestion.postId, finalQuestion.codeSnippet, finalQuestion.weekday, finalQuestion.gmtHour,
        finalQuestion.bodyLength, finalQuestion.titleLength, finalQuestion.url, finalQuestion.isTheSameTopicBTitle,
        finalQuestion.avgUpperCharsPPost, finalQuestion.gratitude, finalQuestion.nTag, finalQuestion.sentimentPositiveScore,
        finalQuestion.sentimentNegativeScore, finalQuestion.commentSentimentPositiveScore,
        finalQuestion.commentSentimentNegativeScore, finalQuestion.successful))

      master ! QuestionWritten
  }
}
