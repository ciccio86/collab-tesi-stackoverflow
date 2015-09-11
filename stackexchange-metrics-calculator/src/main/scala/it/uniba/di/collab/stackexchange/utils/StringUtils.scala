package it.uniba.di.collab.stackexchange.utils

import com.github.nscala_time.time.Imports._

/**
 * Created by francesco on 08/09/15.
 */
object StringUtils {

  implicit class StringImprovements(val s: String) {

    def containsCodeBlock: Boolean = {
      s.matches("<code>.*?</code>")
      true
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
  }

}
