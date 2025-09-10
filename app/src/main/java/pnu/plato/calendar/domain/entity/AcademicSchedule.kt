package pnu.plato.calendar.domain.entity

import java.time.LocalDate

data class AcademicSchedule(
    val title: String,
    val startAt: LocalDate,
    val endAt: LocalDate,
)