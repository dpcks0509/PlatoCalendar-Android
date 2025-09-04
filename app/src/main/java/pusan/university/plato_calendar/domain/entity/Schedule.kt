package pusan.university.plato_calendar.domain.entity

import java.time.LocalDateTime

data class Schedule(
    val uid: String,
    val summary: String?,
    val description: String?,
    val classification: String?,
    val lastModified: LocalDateTime?,
    val timestamp: LocalDateTime?,
    val start: LocalDateTime?,
    val end: LocalDateTime?,
    val categories: String?
)