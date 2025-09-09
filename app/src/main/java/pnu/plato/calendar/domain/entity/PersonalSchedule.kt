package pnu.plato.calendar.domain.entity

import java.time.LocalDateTime

data class PersonalSchedule(
    val id: Long,
    val title: String,
    val description: String?,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val courseCode: String?,
)