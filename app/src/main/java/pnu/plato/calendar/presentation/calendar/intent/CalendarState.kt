package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.common.base.UiState

data class CalendarState(
    val schedules: List<ScheduleUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState