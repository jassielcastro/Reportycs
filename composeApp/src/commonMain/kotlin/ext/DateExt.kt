package ext

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

fun now() = Clock.System.now().toLocalDateTime(TimeZone.UTC).date

fun aYearAgo(): LocalDate = Clock.System.now()
    .toLocalDateTime(TimeZone.UTC)
    .date
    .minus(1, DateTimeUnit.YEAR)

fun LocalDate.formatAsGithub(): String {
    val customFormat = LocalDate.Format {
        year(); chars("-"); monthNumber(); chars("-");dayOfMonth(); chars("T00:00:00Z")
    }
    return this.format(customFormat)
}
