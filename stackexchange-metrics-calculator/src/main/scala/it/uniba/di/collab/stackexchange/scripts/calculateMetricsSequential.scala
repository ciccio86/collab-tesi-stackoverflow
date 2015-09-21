package it.uniba.di.collab.stackexchange.scripts

import java.io._
import java.util.Properties

import grizzled.slf4j.Logging
import com.github.tototoshi.csv.{CSVWriter, CSVReader, DefaultCSVFormat}

import it.uniba.di.collab.stackexchange.utils.StringUtils._
import it.uniba.di.collab.stackexchange.utils.DateUtils._
import org.joda.time.Seconds
import uk.ac.wlv.sentistrength.SentiStrength

import com.github.nscala_time.time.Imports._


/**
 * Created by francesco on 17/09/15.
 */
object calculateMetricsSequential extends Logging {
  def main(args: Array[String]): Unit = {
    if (args.length == 2) {
      val rawQuestionFilePath = args(0)
      val outputFilePath = args(1)

//      val prop = new Properties()
//      prop.load(new FileInputStream(getClass.getResource("/simplelogger.properties").getPath))
//      prop.setProperty("org.slf4j.simpleLogger.logFile", "/tmp/metrics_sequential.log")

      var nonExistentFiles = List[String]()
      if (!new java.io.File(rawQuestionFilePath).exists)
        nonExistentFiles = rawQuestionFilePath :: nonExistentFiles

      if (nonExistentFiles.isEmpty) {
        // TODO: log time of next statement
        val startTimestamp = DateTime.now()
        calculateMetrics(rawQuestionFilePath, outputFilePath)
        val endTimestamp = DateTime.now()
        val executionTime = Seconds.secondsBetween(startTimestamp, endTimestamp).getSeconds
        info(s"Execution Time: $executionTime seconds.")

      } else {
        println("The following files do not exist:\n")
        nonExistentFiles.foreach(println)
        System.exit(1)
      }
    } else {
      println(
        """Please specify the path of the file containing the raw questions as the first parameter
          | and the output file path as the second parameter.""".stripMargin)
      System.exit(1)
    }
  }

  private def calculateMetrics(rawQuestionFilePath: String, outputFilePath: String) = {
    implicit object format extends DefaultCSVFormat {
      override val delimiter: Char = ';'
    }

    val sentiStrength = new SentiStrength()
    val PATH = getClass.getResource("/SentStrength_Data_Sept2011").getPath + "/"
    val ssthInitialisation: Array[String] = Array("sentidata", PATH, "explain")
    sentiStrength.initialise(ssthInitialisation)

    val writer = CSVWriter.open(new File(outputFilePath))(format)
    val reader = CSVReader.open(new File(rawQuestionFilePath))(format)

    writer.writeRow(List("PostId", "CodeSnippet", "Weekday", "GMTHour", "BodyLength", "TitleLength", "URL", "IsTheSameTopicBTitle",
      "AvgUpperCharsPPost", "Gratitude", "NTag", "SentimentPositiveScore", "SentimentNegativeScore", "CommentSentimentPositiveScore",
      "CommentSentimentNegativeScore", "Successful"))

    try {
      val iterator = reader.iteratorWithHeaders

      for (elem <- iterator) {
        // raw metrics
        val questionId = elem("QuestionID")
        val creationDate = elem("CreationDate").toDate
        val title = elem("Title")
        val body = elem("Body")
        val cleanedBody = body.withoutCodeBlocks.stripHtmlTags
        val corpus = title + " " + cleanedBody
        val tags = elem("Tags")
        val cleanedComments = elem("CommentsTexts").withoutCodeBlocks.stripHtmlTags
        val successful = elem("Successful")
        val isTheSameTopicBTitle = elem("IsTheSameTopicBTitle")

        // Calculated Metrics
        val codeSnippet = if (body.containsCodeBlock) "yes" else "no"
        val weekday = creationDate.toWeekDay
        val gmtHour = creationDate.toGMTHour
        val bodyLength = cleanedBody.numberOfWords(false).toString
        val titleLength = title.numberOfWords(false).toString
        val url = body.numberOfUrls.toString
        val avgUpperCharsPPost = corpus.upperCharRatio.toString
        val gratitude = if (cleanedBody.expressGratitude) "yes" else "no"
        val ntag = """<.*?>""".r.findAllIn(tags).length.toString
        val (sentimentPositiveScore, sentimentNegativeScore) = corpus.getSentiment(sentiStrength)
        val (commentSentimentPositiveScore, commentSentimentNegativeScore) = if (cleanedComments.isEmpty) ("NA", "NA") else cleanedComments.getSentiment(sentiStrength)

        // write row in output file

        writer.writeRow(List(questionId, codeSnippet, weekday, gmtHour, bodyLength, titleLength, url, isTheSameTopicBTitle,
          avgUpperCharsPPost, gratitude, ntag, sentimentPositiveScore, sentimentNegativeScore, commentSentimentPositiveScore,
          commentSentimentNegativeScore, successful))

      }


      //iterator.map(elem => (elem("PostId").toInt, elem("IsTheSameTopicBTitle"))).toMap

    } catch {
      case e: Exception => throw e
    } finally {
      writer.close()
      reader.close()
    }
  }
}
