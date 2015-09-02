/**
 * Created by francesco on 01/09/15.
 */
package it.uniba.di.collab.stackexchange.scripts

import com.github.tototoshi.csv._
import java.io._

object filterQuestionIds {
  def main(args: Array[String]) {

    if (args.length > 1) {
      val validQuestionFilePath = args(0)
      val rawQuestionsFilePath = args(1)
      val outputFilePath = args(2)

      val nonExistentFiles = Nil
      if (!new java.io.File(validQuestionFilePath).exists)
        nonExistentFiles :+ validQuestionFilePath
      if (!new java.io.File(rawQuestionsFilePath).exists)
        nonExistentFiles :+ rawQuestionsFilePath
      if (!new java.io.File(outputFilePath).exists)
        nonExistentFiles :+ outputFilePath

      if (nonExistentFiles.isEmpty) {

        //TODO: try
        try {
          val ids: List[Int] = getValidIds(validQuestionFilePath)
          createFilteredCSV(rawQuestionsFilePath, outputFilePath, ids)
        } catch {
          case e: Exception => e.printStackTrace()
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

  def getValidIds(filePath: String): List[Int] = {
    implicit object format extends DefaultCSVFormat {
      override val delimiter: Char = ';'
    }

    val reader = CSVReader.open(new File(filePath))(format)
    try {
      val iterator = reader.iteratorWithHeaders
      iterator.map(elem => elem("PostId").toInt).toList
    } catch {
      case e: Exception => throw e
    } finally {
      reader.close()
    }

  }

  def createFilteredCSV(filePath: String, outputFilePath: String, ids: List[Int]): Unit = {
    implicit object format extends DefaultCSVFormat {
      override val delimiter: Char = ';'
      override val quoteChar: Char = '"'
      override val escapeChar: Char = '\\'
      override val lineTerminator: String = "\n"
    }

    val writer = CSVWriter.open(new File(outputFilePath))(format)

    val reader = CSVReader.open(new File(filePath))(format)

    writer.writeRow(List("QuestionID","CreationDate","Title","Body","Tags","AcceptedDate","NumberOfComments","CommentsTexts","Successful"))
    try {
      val filteredRows = reader.all().tail.filter(fields => ids.contains(fields.head.toInt))
      writer.writeAll(filteredRows)
    } catch {
      case e: Exception => throw e
    } finally {
      writer.close()
      reader.close()
    }
  }
}