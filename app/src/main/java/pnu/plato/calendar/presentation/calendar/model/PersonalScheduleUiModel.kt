package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.PersonalSchedule
import pnu.plato.calendar.presentation.common.theme.CalendarGraphite
import pnu.plato.calendar.presentation.common.theme.CalendarSage
import java.time.LocalDateTime

data class PersonalScheduleUiModel(
    val id: Long,
    val title: String,
    val description: String?,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val courseName: String?,
    val isComplete: Boolean,
    val color: Color,
) {
    constructor(domain: PersonalSchedule, courseName: String?, isComplete: Boolean) : this(
        id = domain.id,
        title = domain.title,
        description = domain.description,
        startAt = domain.startAt,
        endAt = domain.endAt,
        courseName = courseName,
        isComplete = isComplete,
        color = if (!isComplete) CalendarSage else CalendarGraphite
    )
}
