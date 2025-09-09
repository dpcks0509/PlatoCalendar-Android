package pnu.plato.calendar.domain.entity

import java.time.LocalDateTime

sealed class Schedule(
    open val title: String,
    open val description: String?,
    open val memo: String?,
    open val startAt: LocalDateTime,
    open val endAt: LocalDateTime,
    open val isComplete: Boolean
) {

    data class PersonalSchedule(
        val id: Long,
        override val title: String,
        override val description: String?,
        override val memo: String? = null,
        override val startAt: LocalDateTime,
        override val endAt: LocalDateTime,
        override val isComplete: Boolean = false,
        val courseCode: String?,
    ) : Schedule(title, description, memo, startAt, endAt, isComplete)

    data class AcademicSchedule(
        override val title: String,
        override val description: String? = null,
        override val memo: String? = null,
        override val startAt: LocalDateTime,
        override val endAt: LocalDateTime,
        override val isComplete: Boolean = false
    ) : Schedule(title, description, memo, startAt, endAt, isComplete)
}

