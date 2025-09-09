package pnu.plato.calendar.domain.entity

import java.time.LocalTime

data class AcademicSchedule(
    val title: String,
    val memo: String? = null,
    val startAt: LocalTime,
    val endAt: LocalTime,
    val isComplete: Boolean = false
)