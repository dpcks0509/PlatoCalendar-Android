package pusan.university.plato_calendar.presentation.common.function

import java.time.LocalDateTime

fun String.parseIcsToLocalDateTime(): LocalDateTime {
    val year = substring(0, 4).toInt()
    val month = substring(4, 6).toInt()
    val day = substring(6, 8).toInt()
    val hour = substring(9, 11).toInt()
    val minute = substring(11, 13).toInt()

    return LocalDateTime.of(year, month, day, hour, minute)
}
