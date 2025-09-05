package pusan.university.plato_calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pusan.university.plato_calendar.domain.entity.Schedule
import pusan.university.plato_calendar.presentation.common.function.parseUctToLocalDateTime
import java.time.LocalDateTime

data class ScheduleUiModel(
    val id: String,
    val isComplete: Boolean,
    val deadLine: LocalDateTime?,
    val courseName: String?,
    val title: String?,
    val color: Color?
) {
    constructor(domain: Schedule, courseName: String?) : this(
        id = domain.uid,
        isComplete = false,
        deadLine = domain.end?.parseUctToLocalDateTime(),
        courseName = courseName,
        title = domain.summary,
        color = null
    )
}