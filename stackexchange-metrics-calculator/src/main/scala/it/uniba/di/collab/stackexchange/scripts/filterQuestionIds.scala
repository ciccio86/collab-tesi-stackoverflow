/**
 * Created by francesco on 01/09/15.
 */
package it.uniba.di.collab.stackexchange.scripts

import com.github.tototoshi.csv._
import java.io._

object filterQuestionIds {
  def main(args: Array[String]) {

    if (args.length == 3) {
      val validQuestionFilePath = args(0)
      val rawQuestionsFilePath = args(1)
      val outputFilePath = args(2)

      var nonExistentFiles = List[String]()
      if (!new java.io.File(validQuestionFilePath).exists)
        nonExistentFiles = validQuestionFilePath :: nonExistentFiles
      if (!new java.io.File(rawQuestionsFilePath).exists)
        nonExistentFiles = rawQuestionsFilePath :: nonExistentFiles

      if (nonExistentFiles.isEmpty) {

        try {
          val ids: Map[Int, String] = getValidIds(validQuestionFilePath)
          createFilteredCSV(rawQuestionsFilePath, outputFilePath, ids)
        } catch {
          case e: Exception => println(e.printStackTrace());
        }

      } else {
        println("The following files do not exist:\n")
        nonExistentFiles.foreach(println)
        System.exit(1)
      }

    } else {
      println(
        """Please specify the path of the file containing di ids of the valid questions as the first parameter,
          | the raw questions file as the second parameter
          | and the output file path as the third parameter.""".stripMargin)
      System.exit(1)
    }
  }

  // Takes the ids and 'IsTheSameTopicBTitle' of the valid questions
  def getValidIds(filePath: String): Map[Int, String] = {
    implicit object format extends DefaultCSVFormat {
      override val delimiter: Char = ';'
    }

    val reader = CSVReader.open(new File(filePath))(format)
    try {
      val iterator = reader.iteratorWithHeaders
      iterator.map(elem => (elem("PostId").toInt, elem("IsTheSameTopicBTitle"))).toMap
    } catch {
      case e: Exception => throw e
    } finally {
      reader.close()
    }

  }

  def createFilteredCSV(filePath: String, outputFilePath: String, ids: Map[Int, String]): Unit = {
    implicit object format extends DefaultCSVFormat {
      override val delimiter: Char = ';'
      //override val quoteChar: Char = '"'
      //override val escapeChar: Char = '\\'
      override val lineTerminator: String = "\n"
    }

    val writer = CSVWriter.open(new File(outputFilePath))(format)

    val reader = CSVReader.open(new File(filePath))(format)

    writer.writeRow(List("QuestionID","CreationDate","Title","Body","Tags","AcceptedDate","NumberOfComments","CommentsTexts","Successful","IsTheSameTopicBTitle"))
    try {
      reader.readNext()
      reader.foreach(fields => {
        val id = fields.head.toInt
        if(ids.contains(id)) {
          // Append the IsTheSameTopicBTitle
          val newFields = fields :+ ids(id)
          writer.writeRow(newFields)
        }
      })
    } catch {
      case e: Exception => throw e
    } finally {
      writer.close()
      reader.close()
    }
  }
}