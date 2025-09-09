package pnu.plato.calendar.domain.entity

import java.time.LocalTime

data class AcademicSchedule(
    val title: String,
    val startAt: LocalTime,
    val endAt: LocalTime,
    val memo: String? = null,
    val isComplete: Boolean = false
)