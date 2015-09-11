package it.uniba.di.collab.stackexchange.utils

import com.github.nscala_time.time.Imports._
import org.joda.time.DateTimeConstants

/**
 * Created by francesco on 08/09/15.
 */
object DateUtils {
  implicit class DateImprovements(val d: DateTime) {

    def toWeekDay: String = {
      val weekDay = d.getDayOfWeek
      weekDay match {
        case wd @ (DateTimeConstants.SATURDAY | DateTimeConstants.SUNDAY) => "Weekend"
        case _ => "Weekday"
      }
    }

    def toGMTHour: String = {
      val hour = d.getHourOfDay
      hour match {
        case (6 | 7 | 8 | 9 | 10 | 11) => "Morning"
        case (12 | 13 | 14 | 15 | 16 | 17) => "Afternoon"
        case (18 | 19 | 20 | 21 | 22) => "Evening"
        case (23 | 0 | 1 | 2 | 3 | 4 | 5) => "Night"
      }
    }

  }
}
