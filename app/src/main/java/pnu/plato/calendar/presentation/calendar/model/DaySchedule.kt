package pnu.plato.calendar.presentation.calendar.model

import pnu.plato.calendar.presentation.calendar.model.DayOfWeekUiModel.Companion.isWeekend
import java.time.LocalDate

data class DaySchedule(
    val date: LocalDate,
    val isToday: Boolean,
    val isSelected: Boolean,
    val isInMonth: Boolean,
    val schedules: List<ScheduleUiModel>,
) {
    val isWeekend: Boolean = date.dayOfWeek.isWeekend()
}
