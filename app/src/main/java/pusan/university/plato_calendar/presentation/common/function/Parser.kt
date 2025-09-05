package pusan.university.plato_calendar.presentation.common.function

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun String.parseIcsToLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
    val instant = Instant.from(formatter.parse(this))
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}