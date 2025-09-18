package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.base.UiState
import java.time.LocalDate

data class CalendarState(
    val today: LocalDate = LocalDate.now(),
    val selectedDate: LocalDate = today,
    val currentYearMonth: YearMonth = YearMonth(year = today.year, month = today.monthValue),
    val schedules: List<ScheduleUiModel> = emptyList(),
    val isLoading: Boolean = false,
) : UiState
