package it.uniba.di.collab.stackexchange.scripts

/**
 * Created by francesco on 10/09/15.
 */

import java.io.{FileWriter, File}

import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}
import uk.ac.wlv.sentistrength._
import it.uniba.di.collab.stackexchange.utils.StringUtils._
import it.uniba.di.collab.stackexchange.utils.IntUtils._

object checkSentiment {

  case class OldComputedQuestion(id: String, positiveSentiment: Int, negativeSentiment: Int)
  case class NotComputedQuestion(id: Int, title: String, body: String)

  def main(args: Array[String]): Unit = {

    if (args.length > 1) {
      val oldComputedQuestionsFilePath = args(0)
      val notComputedQuestionsFilePath = args(1)
      val outputFilePath = args(2)

      var nonExistentFiles = List[String]()
      if (!new java.io.File(oldComputedQuestionsFilePath).exists)
         nonExistentFiles = oldComputedQuestionsFilePath :: nonExistentFiles
      if (!new java.io.File(notComputedQuestionsFilePath).exists)
        nonExistentFiles = notComputedQuestionsFilePath :: nonExistentFiles

      if (nonExistentFiles.isEmpty) {
        try {
          val sentiStrength = new SentiStrength()
          val PATH = getClass.getResource("/SentStrength_Data_Sept2011").getPath + "/"
          val ssthInitialisation: Array[String] = Array("sentidata", PATH, "explain")
          sentiStrength.initialise(ssthInitialisation)

          val oldQuestions: Map[Int, (Int, Int)] = getOldComputedQuestions(oldComputedQuestionsFilePath)
          val newQuestions: List[NotComputedQuestion] = getNotComputedQuestions(notComputedQuestionsFilePath)
          compareSentiment(oldQuestions, newQuestions, sentiStrength, outputFilePath)

        } catch {
          case e: Exception => println(e.printStackTrace)
        }
      } else {
        println("The following files do not exist:\n")
        nonExistentFiles.foreach(println)
        System.exit(1)
      }

    } else {
      println("Please specify the path of the file containing di ids of the valid questions as the first parameter and the raw questions file as the second parameter.")
      System.exit(1)
    }

  }

  def getOldComputedQuestions(filePath: String): Map[Int, (Int, Int)] = {
    implicit object format extends DefaultCSVFormat {
      override val delimiter: Char = ';'
    }

    val reader = CSVReader.open(new File(filePath))(format)
    try {
      val iterator = reader.iteratorWithHeaders
      iterator.map(elem => {
        val id = elem("PostId").toInt
        val positive = elem("SentimentPositiveScore").toInt
        val negative = elem("SentimentNegativeScore").toInt
        (id, (positive, negative))
      }).toMap
    } catch {
      case e: Exception => throw e
    } finally {
      reader.close()
    }
  }

  def getNotComputedQuestions(filePath: String): List[NotComputedQuestion] = {
    implicit object format extends DefaultCSVFormat {
      override val delimiter: Char = ';'
      //override val quoteChar: Char = '"'
      //override val escapeChar: Char = '\\'
      override val lineTerminator: String = "\n"
    }

    val reader = CSVReader.open(new File(filePath))(format)
    val prova = reader.all().tail

    prova.map(row => {
      val id = row(0).toInt
      val title = row(2)
      val body = row(3)
      NotComputedQuestion(id, title, body)
    })
//    try {
//      val iterator = reader.iteratorWithHeaders
//      val list = iterator.map(elem => {
//        val id = elem("QuestionID").toInt
//        val title = elem("Title")
//        val body = elem("Body")
//        new NotComputedQuestion(id, title, body)
//      })
//      list.toList
//    } catch {
//      case e: Exception => throw e
//    } finally {
//      reader.close()
//    }
  }

  def compareSentiment(oldQuestions: Map[Int, (Int, Int)], newQuestions: List[NotComputedQuestion], sentiStrength: SentiStrength, outputFilePath: String) = {

    val file = new File(outputFilePath)
    file.getParentFile.mkdirs()
    val writer = new FileWriter(file)
    try{
      newQuestions.foreach(newQuestion =>
      {
        val bodyParsed = newQuestion.body.withoutCodeBlocks.stripHtmlTags
        val corpus = newQuestion.title + " " + bodyParsed
        val rawSentimentScores = sentiStrength.computeSentimentScores(corpus).split(" ").take(2)
        val Array(positive, negative) = rawSentimentScores.map(_.toInt.normalizeSentimentScore)
        val oldQuestion = oldQuestions(newQuestion.id)
        // Checks if the sentiment score are equal to old question
        if(oldQuestion._1 != positive || oldQuestion._2 != negative){
          val line = s"Question #${newQuestion.id} => OldPositive = ${oldQuestion._1}; NewPositive = $positive; OldNegative = ${oldQuestion._2}; NewNegative = $negative"
          writer.write(line + "\n")
        }
      })
    } catch {
      case e: Exception => throw e
    } finally {
      writer.close()
    }
  }
}
