package it.uniba.di.collab.stackexchange.actorsystem.actors

import akka.actor.{ActorLogging, Actor}
import it.uniba.di.collab.stackexchange.actorsystem.messages.Messages._
import uk.ac.wlv.sentistrength.SentiStrength

import it.uniba.di.collab.stackexchange.utils.StringUtils._

class Worker(forWeka: Boolean) extends Actor with ActorLogging {

  private val sentiStrength = new SentiStrength()
  val PATH = getClass.getResource("/SentStrength_Data_Sept2011").getPath + "/"
  val ssthInitialisation: Array[String] = Array("sentidata", PATH, "explain")
  sentiStrength.initialise(ssthInitialisation)

  private val writer = context.actorSelection("/user/master/writer")

  def receive = {
    case rawQuestion: RawQuestion =>


      val cleanedBody = rawQuestion.body.withoutCodeBlocks.stripHtmlTags.stripNewlineChars
      val (sentimentPositiveScore, sentimentNegativeScore) = cleanedBody.getSentiment(sentiStrength)

      writer ! FinalDatasetQuestion(rawQuestion.id, rawQuestion.`type` , cleanedBody, sentimentPositiveScore, sentimentNegativeScore)

      sender ! QuestionProcessed

    case message =>
      println(s"Worker: Unknown message received: $message .")
  }
}
