package pnu.plato.calendar.domain.entity

import java.time.LocalDateTime

sealed interface Schedule {
    data class StudentSchedule(
        val title: String,
        val description: String?,
        val startAt: LocalDateTime,
        val endAt: LocalDateTime,
        val courseCode: String,
    ) : Schedule

    data class AcademicSchedule(
        val title: String,
        val startAt: LocalDateTime,
        val endAt: LocalDateTime,
    ) : Schedule
}
