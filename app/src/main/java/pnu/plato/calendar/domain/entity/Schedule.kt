package pnu.plato.calendar.domain.entity

import java.time.LocalDateTime

sealed class Schedule(
    open val title: String,
    open val description: String?,
    open val memo: String?,
    open val startAt: LocalDateTime,
    open val endAt: LocalDateTime,
) {
    data class PersonalSchedule(
        override val title: String,
        override val description: String?,
        override val memo: String? = null,
        override val startAt: LocalDateTime,
        override val endAt: LocalDateTime,
        val courseCode: String?,
    ) : Schedule(title, description, memo, startAt, endAt)

    data class AcademicSchedule(
        override val title: String,
        override val description: String? = null,
        override val memo: String? = null,
        override val startAt: LocalDateTime,
        override val endAt: LocalDateTime,
    ) : Schedule(title, description, memo, startAt, endAt)

    data class CustomSchedule(
        override val title: String,
        override val description: String?,
        override val memo: String?,
        override val startAt: LocalDateTime,
        override val endAt: LocalDateTime,
    ) : Schedule(title, description, memo, startAt, endAt)
}
