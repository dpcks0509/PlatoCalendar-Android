package pusan.university.plato_calendar.domain.entity

data class Schedule(
    val uid: String,
    val summary: String?,
    val description: String?,
    val classification: String?,
    val lastModified: String?,
    val timestamp: String?,
    val start: String?,
    val end: String?,
    val categories: String?
)