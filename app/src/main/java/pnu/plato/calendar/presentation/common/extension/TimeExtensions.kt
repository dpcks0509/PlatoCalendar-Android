package pnu.plato.calendar.presentation.common.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val amPmTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)

fun LocalDateTime.formatTimeWithMidnightSpecialCase(): String {
    return if (hour == 0) {
        "00시 ${minute.toString().padStart(2, '0')}분"
    } else {
        this.format(amPmTimeFormatter)
    }
}
