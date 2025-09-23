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

    sealed class PersonalSchedule : Schedule() {
        abstract val id: Long
        abstract val description: String?
        abstract val startAt: LocalDateTime
        abstract val endAt: LocalDateTime

        data class CourseSchedule(
            override val id: Long,
            override val title: String,
            override val description: String?,
            override val startAt: LocalDateTime,
            override val endAt: LocalDateTime,
            val courseCode: String,
        ) : PersonalSchedule()

        data class CustomSchedule(
            override val id: Long,
            override val title: String,
            override val description: String?,
            override val startAt: LocalDateTime,
            override val endAt: LocalDateTime,
        ) : PersonalSchedule()
    }
}
