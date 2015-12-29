package it.uniba.di.collab.stackexchange.actorsystem.actors

import akka.actor.{Actor, Props}
import com.github.tototoshi.csv.CSVReader
import it.uniba.di.collab.stackexchange.actorsystem.messages.Messages._

/**
 * Created by francesco on 24/09/15.
 */
class ReaderActor(reader: CSVReader, numberOfWorkers: Int, forWeka: Boolean) extends Actor {

  val router = context.actorOf(Props(classOf[RouterActor], numberOfWorkers, forWeka), "router")
  private val iterator = reader.iteratorWithHeaders
  private val master = context.parent
  private var isReaderOpen = true


  def receive = {
    case Start =>
      0 to (numberOfWorkers * 10) foreach { _ =>
        processNextQuestion()
      }

    case QuestionProcessed =>
      processNextQuestion()

  }

  def processNextQuestion(): Unit = {

    try {
      if (isReaderOpen && iterator.hasNext) {
        val elem = iterator.next()

        val id = elem("Id")
        val body = elem("Body")
        val `type` = elem("Type")

        router ! RawQuestion(id, body, `type`)

        // send message to master to notify that a question was read
        master ! QuestionRead
      } else {
        // send message to master to notify that we finished reading questions
        master ! LastQuestionRead
        reader.close()
        isReaderOpen = false
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        reader.close()
        isReaderOpen = false
    }
  }
}
