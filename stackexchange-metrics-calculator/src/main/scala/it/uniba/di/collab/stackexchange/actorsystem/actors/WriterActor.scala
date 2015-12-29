package it.uniba.di.collab.stackexchange.actorsystem.actors

import akka.actor.Actor
import com.github.tototoshi.csv.CSVWriter
import it.uniba.di.collab.stackexchange.actorsystem.messages.Messages._

/**
 * Created by francesco on 24/09/15.
 */
class WriterActor(writer: CSVWriter) extends Actor {

  private val master = context.parent


  writer.writeRow(List("Id", "Type", "CleanedBody", "SentimentPositiveScore", "SentimentNegativeScore"))

  def receive = {
    case finalQuestion: FinalDatasetQuestion =>
      writer.writeRow(List(finalQuestion.id, finalQuestion.`type`, finalQuestion.cleanedBody, finalQuestion.sentimentPositiveScore,
        finalQuestion.sentimentNegativeScore))

      master ! QuestionWritten
  }
}
