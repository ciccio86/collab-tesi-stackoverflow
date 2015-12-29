package it.uniba.di.collab.stackexchange.utils

import scala.util.control.Exception._

import com.github.nscala_time.time.Imports._
import uk.ac.wlv.sentistrength.SentiStrength

import it.uniba.di.collab.stackexchange.utils.IntUtils._

/**
 * Created by francesco on 08/09/15.
 */
object StringUtils {

  implicit class StringImprovements(val s: String) {

    def stripNewlineChars: String = {
      val pattern = "(\\n+\\r*)+"
      s.replaceAll(pattern, " ")
    }

    def containsCodeBlock: Boolean = {
      val pattern = "<code>.*?</code>".r
      (pattern findFirstIn s).isDefined
    }

    def withoutCodeBlocks: String = {
      val pattern = "<code>.*?</code>"
      s.replaceAll(pattern, "")
    }

    def toDate: DateTime = {
      val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
      formatter.parseDateTime(s)
      //TODO: manage parse errors
    }

    def numberOfWords(stripHtmlTags: Boolean = true): Int = {

      val text: String = if (stripHtmlTags) s.stripHtmlTags else s

      val wordPattern = """[\w']+""".r
      wordPattern.findAllIn(text).length
    }

    // remove <a> tags with content then strip all other tags
    def stripHtmlTags: String = {
      val pattern = "<a.*?</a>"
      val withoutLinks = s.replaceAll(pattern, "")

      import org.jsoup._
      Jsoup.parse(withoutLinks).text()
    }

    def numberOfUrls: Int = {
      val pattern = """<a.*?</a>""".r
      pattern.findAllIn(s).length
    }

    def upperCharRatio: Double = {

      // NOTE: old metric is calculated counting in total chars whitespace
      // this does not count whitespace

      val wordPattern = """[\w]+""".r
      val allChars = wordPattern.findAllIn(s) mkString ""
      val totalChars = allChars.length

      if (totalChars == 0)
        totalChars
      else {
        val uppercaseChars = allChars.count(_.isUpper)
        uppercaseChars.toDouble / totalChars
      }
    }

    def expressGratitude: Boolean = {
      val gratitudeTerms = List("thanks", "thx", "thanx", "thank you")
      gratitudeTerms.exists(s.toLowerCase.contains)
    }

    def getSentiment(sentiStrength: SentiStrength): (String, String) = {
      val rawSentimentScores = sentiStrength.computeSentimentScores(s).split(" ").take(2)
      val Array(positive, negative) = rawSentimentScores.map(_.toInt.normalizeSentimentScore)
      (positive.toString, negative.toString)
    }

    def toIntOpt = catching(classOf[NumberFormatException]) opt s.toInt
  }

}
