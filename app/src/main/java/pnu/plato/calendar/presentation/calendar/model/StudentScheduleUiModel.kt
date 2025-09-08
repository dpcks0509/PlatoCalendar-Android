package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.Schedule.StudentSchedule
import pnu.plato.calendar.presentation.common.theme.CalendarColors
import java.time.LocalDateTime

data class StudentScheduleUiModel(
    val title: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val courseName: String,
    val isComplete: Boolean,
    val color: Color,
) {
    constructor(domain: StudentSchedule, courseName: String) : this(
        title = domain.title,
        startAt = domain.startAt,
        endAt = domain.endAt,
        courseName = courseName,
        isComplete = false,
        color = CalendarColors.random(),
    )
}
