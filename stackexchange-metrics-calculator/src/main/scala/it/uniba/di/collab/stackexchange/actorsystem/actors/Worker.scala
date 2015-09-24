package it.uniba.di.collab.stackexchange.actorsystem.actors

import akka.actor.{ActorLogging, Actor}
import it.uniba.di.collab.stackexchange.actorsystem.messages.Messages.{FinalDatasetQuestion, RawQuestion}
import uk.ac.wlv.sentistrength.SentiStrength

import it.uniba.di.collab.stackexchange.utils.StringUtils._
import it.uniba.di.collab.stackexchange.utils.DateUtils._

class Worker extends Actor with ActorLogging {

  private val sentiStrength = new SentiStrength()
  val PATH = getClass.getResource("/SentStrength_Data_Sept2011").getPath + "/"
  val ssthInitialisation: Array[String] = Array("sentidata", PATH, "explain")
  sentiStrength.initialise(ssthInitialisation)

  def receive = {
    case rawQuestion: RawQuestion =>
      log.debug(rawQuestion.toString)

      val codeSnippet = if (rawQuestion.body.containsCodeBlock) "yes" else "no"
      val creationDate = rawQuestion.creationDate.toDate
      val title = rawQuestion.title
      val body = rawQuestion.body
      val weekday = creationDate.toWeekDay
      val gmtHour = creationDate.toGMTHour
      val cleanedBody = body.withoutCodeBlocks.stripHtmlTags
      val corpus = title + " " + cleanedBody
      val bodyLength = cleanedBody.numberOfWords(false).toString
      val titleLength = title.numberOfWords(false).toString
      val url = body.numberOfUrls.toString
      val avgUpperCharsPPost = corpus.upperCharRatio.toString
      val gratitude = if (cleanedBody.expressGratitude) "yes" else "no"
      val nTag = """<.*?>""".r.findAllIn(rawQuestion.tags).length.toString
      val (sentimentPositiveScore, sentimentNegativeScore) = corpus.getSentiment(sentiStrength)
      val (commentSentimentPositiveScore, commentSentimentNegativeScore) = if (rawQuestion.commentsText.isEmpty) ("NA", "NA") else rawQuestion.commentsText.getSentiment(sentiStrength)

      sender ! FinalDatasetQuestion(rawQuestion.questionId, codeSnippet, weekday, gmtHour, bodyLength, titleLength,
        url, rawQuestion.isTheSameTopicBTitle, avgUpperCharsPPost, gratitude, nTag, sentimentPositiveScore, sentimentNegativeScore,
        commentSentimentPositiveScore, commentSentimentNegativeScore, rawQuestion.successful)

    case message =>
      println(s"Worker: Unknown message received: $message .")
  }
}
