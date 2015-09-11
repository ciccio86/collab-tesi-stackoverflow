package it.uniba.di.collab.stackexchange.utils

/**
 * Created by francesco on 10/09/15.
 */
object IntUtils {
  implicit class IntImprovements(val i: Int) {
    def normalizeSentimentScore: Int = {
      i match {
        case 0 => 0
        case _ => Math.abs(i) - 1
      }
    }
  }
}
