package pnu.plato.calendar.presentation.common.extension

import java.time.LocalDateTime

fun LocalDateTime.formatTimeWithMidnightSpecialCase(): String {
    val (period, displayHour) =
        when (hour) {
            0 -> "오전" to "00" // 00시 → 오전 00시
            in 1..11 -> "오전" to hour.toString().padStart(2, '0') // 01~11시 → 오전 01~11시
            12 -> "오전" to "12" // 12시 → 오전 12시
            else -> "오후" to (hour - 12).toString().padStart(2, '0') // 13~23시 → 오후 01~11시
        }
    val displayMinute = minute.toString().padStart(2, '0')

    return "$period ${displayHour}시 ${displayMinute}분"
}
