package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.AcademicSchedule
import pnu.plato.calendar.presentation.common.theme.CalendarSage
import java.time.LocalTime

data class AcademicScheduleUiModel(
    val title: String,
    val startAt: LocalTime,
    val endAt: LocalTime,
    val color: Color,
) {
    constructor(domain: AcademicSchedule) : this(
        title = domain.title,
        startAt = domain.startAt,
        endAt = domain.endAt,
        color = CalendarSage
    )
}
