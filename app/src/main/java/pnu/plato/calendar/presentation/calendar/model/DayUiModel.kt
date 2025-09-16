package pnu.plato.calendar.presentation.calendar.model

import java.time.LocalDate

data class DayUiModel(
    val date: LocalDate,
    val isToday: Boolean,
    val isSelected: Boolean,
    val schedules: List<ScheduleUiModel>,
)
