package it.uniba.di.collab.stackexchange.actorsystem.actors

import java.io.File

import akka.actor.{ActorRef, Props, Actor}
import com.github.tototoshi.csv.{CSVReader, CSVWriter, DefaultCSVFormat}
import it.uniba.di.collab.stackexchange.actorsystem.messages.Messages._

import it.uniba.di.collab.stackexchange.utils.StringUtils._

import com.github.nscala_time.time.Imports._
import org.joda.time.Seconds

class Master(rawQuestionsPath: String, outputFilePath: String, numberOfWorkers: Int) extends Actor {

  private var totalQuestions = 0
  private var questionsProcessed = 0
  private var startTimestamp = DateTime.now()
  private var fileSender: Option[ActorRef] = None

  implicit object format extends DefaultCSVFormat {
    override val delimiter: Char = ';'
  }

  val writer = CSVWriter.open(new File(outputFilePath))(format)
  val reader = CSVReader.open(new File(rawQuestionsPath))(format)

  writer.writeRow(List("PostId", "CodeSnippet", "Weekday", "GMTHour", "BodyLength", "TitleLength", "URL", "IsTheSameTopicBTitle",
    "AvgUpperCharsPPost", "Gratitude", "NTag", "SentimentPositiveScore", "SentimentNegativeScore", "CommentSentimentPositiveScore",
    "CommentSentimentNegativeScore", "Successful"))

  def receive = {
    case Start =>

      try {
        fileSender = Some(sender())
        val iterator = reader.iteratorWithHeaders

        val router = context.actorOf(Props(classOf[RouterActor], numberOfWorkers ))

        startTimestamp = DateTime.now()

        for (elem <- iterator) {
          totalQuestions += 1
          val questionId = elem("QuestionID")
          val creationDate = elem("CreationDate")
          val title = elem("Title")
          val body = elem("Body")
          val tags = elem("Tags")
          val cleanedComments = elem("CommentsTexts").withoutCodeBlocks.stripHtmlTags
          val successful = elem("Successful")
          val isTheSameTopicBTitle = elem("IsTheSameTopicBTitle")

          router ! RawQuestion(questionId, creationDate, title, body, tags, cleanedComments, successful, isTheSameTopicBTitle)

        }
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        reader.close()
      }

    case finalQuestion: FinalDatasetQuestion =>
      writer.writeRow(List(finalQuestion.postId, finalQuestion.codeSnippet, finalQuestion.weekday, finalQuestion.gmtHour,
        finalQuestion.bodyLength, finalQuestion.titleLength, finalQuestion.url, finalQuestion.isTheSameTopicBTitle,
        finalQuestion.avgUpperCharsPPost, finalQuestion.gratitude, finalQuestion.nTag, finalQuestion.sentimentPositiveScore,
        finalQuestion.sentimentNegativeScore, finalQuestion.commentSentimentPositiveScore,
        finalQuestion.commentSentimentNegativeScore, finalQuestion.successful))

      questionsProcessed += 1

//      if(questionsProcessed % 5000 == 0)
//        println(s"$questionsProcessed / $totalQuestions processed.")

      if(questionsProcessed == totalQuestions) {
        writer.close()
        val endTimestamp = DateTime.now()
        //val executionTime = endTimestamp.getMillis - startTimestamp.getMillis
        val executionTime = Seconds.secondsBetween(startTimestamp, endTimestamp).getSeconds
        fileSender.map(_ ! s"$questionsProcessed;$numberOfWorkers;$executionTime")
        //fileSender.map(_ ! s"$questionsProcessed questions processed with $numberOfWorkers workers in $executionTime milliseconds.")
      }
  }
}
