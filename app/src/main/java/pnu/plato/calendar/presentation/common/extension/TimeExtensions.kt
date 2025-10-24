package pnu.plato.calendar.presentation.common.extension

import java.time.LocalDateTime

fun LocalDateTime.formatTimeWithMidnightSpecialCase(): String {
    val (period, displayHour) =
        when (hour) {
            0 -> "오전" to "00"
            in 1..11 -> "오전" to hour.toString().padStart(2, '0')
            12 -> "오전" to "12"
            else -> "오후" to (hour - 12).toString().padStart(2, '0')
        }
    val displayMinute = minute.toString().padStart(2, '0')

    return "$period ${displayHour}시 ${displayMinute}분"
}
