package it.uniba.di.collab.stackexchange.actorsystem.actors

import java.io.File

import akka.actor.{Props, Actor}
import com.github.tototoshi.csv.CSVReader
import it.uniba.di.collab.stackexchange.actorsystem.messages.Messages._
import it.uniba.di.collab.stackexchange.utils.StringUtils._

/**
 * Created by francesco on 24/09/15.
 */
class ReaderActor(reader: CSVReader, numberOfWorkers: Int) extends Actor {

  val router = context.actorOf(Props(classOf[RouterActor], numberOfWorkers), "router")
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

        val questionId = elem("QuestionID")
        val creationDate = elem("CreationDate")
        val title = elem("Title")
        val body = elem("Body")
        val tags = elem("Tags")
        val cleanedComments = elem("CommentsTexts").withoutCodeBlocks.stripHtmlTags
        val successful = elem("Successful")
        val isTheSameTopicBTitle = elem("IsTheSameTopicBTitle")

        router ! RawQuestion(questionId, creationDate, title, body, tags, cleanedComments, successful, isTheSameTopicBTitle)

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
