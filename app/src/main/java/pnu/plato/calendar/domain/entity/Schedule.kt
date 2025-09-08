package pnu.plato.calendar.domain.entity

import java.time.LocalDateTime

sealed interface Schedule {

    data class StudentSchedule(
        val id: String,
        val title: String,
        val description: String?,
        val deadLine: LocalDateTime,
        val courseCode: String?
    ) : Schedule

    data class AcademicSchedule(
        val id: String,
    ) : Schedule
}