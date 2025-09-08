package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.Schedule
import pnu.plato.calendar.presentation.common.theme.CalendarLavender
import pnu.plato.calendar.presentation.common.theme.CalendarSage
import pnu.plato.calendar.presentation.common.theme.CalendarTomato
import java.time.LocalDateTime

data class ScheduleUiModel(
    val title: String,
    val description: String?,
    val memo: String?,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val courseName: String?,
    val isComplete: Boolean,
    val color: Color,
) {
    constructor(domain: Schedule, courseName: String?) : this(
        title = domain.title,
        description = domain.description,
        memo = domain.memo,
        startAt = domain.startAt,
        endAt = domain.endAt,
        courseName = courseName,
        isComplete = false,
        color =
            when (domain) {
                is Schedule.PersonalSchedule -> CalendarSage
                is Schedule.AcademicSchedule -> CalendarLavender
                is Schedule.CustomSchedule -> CalendarTomato
            },
    )
}
