package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.PersonalSchedule
import pnu.plato.calendar.presentation.common.theme.CalendarGraphite
import pnu.plato.calendar.presentation.common.theme.CalendarSage
import java.time.LocalDateTime

data class PersonalScheduleUiModel(
    val title: String,
    val description: String?,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val memo: String?,
    val isComplete: Boolean,
    val courseName: String?,
    val color: Color,
) {
    constructor(domain: PersonalSchedule, courseName: String?) : this(
        title = domain.title,
        description = domain.description,
        startAt = domain.startAt,
        endAt = domain.endAt,
        memo = domain.memo,
        isComplete = domain.isComplete,
        courseName = courseName,
        color = if (!domain.isComplete) CalendarSage else CalendarGraphite
    )
}
