package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.Schedule
import pnu.plato.calendar.presentation.common.theme.CalendarColors
import java.time.LocalDateTime

data class ScheduleUiModel(
    val id: String,
    val title: String,
    val deadLine: LocalDateTime,
    val courseName: String?,
    val isComplete: Boolean,
    val color: Color
) {
    constructor(domain: Schedule, courseName: String?) : this(
        id = domain.id,
        title = domain.title,
        deadLine = domain.deadLine,
        courseName = courseName,
        isComplete = false,
        color = CalendarColors.random()
    )
}