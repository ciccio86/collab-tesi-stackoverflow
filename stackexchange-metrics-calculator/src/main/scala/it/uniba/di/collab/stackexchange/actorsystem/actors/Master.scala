package it.uniba.di.collab.stackexchange.actorsystem.actors

import java.io.File

import akka.actor.{ActorLogging, ActorRef, Props, Actor}
import com.github.tototoshi.csv.{CSVReader, CSVWriter, DefaultCSVFormat}
import it.uniba.di.collab.stackexchange.actorsystem.messages.Messages._

import com.github.nscala_time.time.Imports._
import org.joda.time.Seconds

class Master(rawQuestionsPath: String, outputFilePath: String, numberOfWorkers: Int, forWeka: Boolean) extends Actor with ActorLogging {

  implicit object format extends DefaultCSVFormat {
    override val delimiter: Char = ';'
    override val escapeChar: Char= '\\'
  }

  implicit object writerFormat extends DefaultCSVFormat {
    override val delimiter: Char = if (forWeka) ',' else ';'
  }

  val reader = CSVReader.open(new File(rawQuestionsPath))(format)
  val writer = CSVWriter.open(new File(outputFilePath))(writerFormat)

  val readerActor = context.actorOf(Props(classOf[ReaderActor], reader, numberOfWorkers, forWeka), "reader")
  val writerActor = context.actorOf(Props(classOf[WriterActor], writer), "writer")

  private var readQuestions = 0
  private var writtenQuestions = 0
  private var allQuestionsRead = false
  private val startTimestamp = DateTime.now()
  private var fileSender: Option[ActorRef] = None

  // Create Reader and Actor Writers


  def receive = {
    case Start =>
      fileSender = Some(sender())
      readerActor ! Start

    case QuestionRead =>
      readQuestions += 1

    case LastQuestionRead =>
      allQuestionsRead = true

    case QuestionWritten =>
      writtenQuestions += 1
//      if(questionsProcessed % 5000 == 0)
//        println(s"$questionsProcessed / $totalQuestions processed.")
      if(allQuestionsRead && readQuestions == writtenQuestions) {
        writer.close()
        val endTimestamp = DateTime.now()
        val executionTime = Seconds.secondsBetween(startTimestamp, endTimestamp).getSeconds
        fileSender.map(_ ! s"$writtenQuestions;$numberOfWorkers;$executionTime")
      }
  }
}
