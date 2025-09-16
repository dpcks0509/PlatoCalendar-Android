package pnu.plato.calendar.domain.entity

import java.time.LocalDate
import java.time.LocalDateTime

sealed class Schedule {
    abstract val title: String

    data class AcademicSchedule(
        override val title: String,
        val startAt: LocalDate,
        val endAt: LocalDate,
    ) : Schedule()

    data class PersonalSchedule(
        val id: Long,
        override val title: String,
        val description: String?,
        val startAt: LocalDateTime,
        val endAt: LocalDateTime,
        val courseCode: String?,
    ) : Schedule()
}
