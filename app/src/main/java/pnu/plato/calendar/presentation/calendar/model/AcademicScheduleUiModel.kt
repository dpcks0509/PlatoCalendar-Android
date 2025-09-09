package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.AcademicSchedule
import pnu.plato.calendar.presentation.common.theme.CalendarGraphite
import pnu.plato.calendar.presentation.common.theme.CalendarSage
import java.time.LocalTime

data class AcademicScheduleUiModel(
    val title: String,
    val startAt: LocalTime,
    val endAt: LocalTime,
    val memo: String?,
    val isComplete: Boolean,
    val color: Color,
) {
    constructor(domain: AcademicSchedule) : this(
        title = domain.title,
        startAt = domain.startAt,
        endAt = domain.endAt,
        memo = domain.memo,
        isComplete = domain.isComplete,
        color = if (!domain.isComplete) CalendarSage else CalendarGraphite
    )
}
