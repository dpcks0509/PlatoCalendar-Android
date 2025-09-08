package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.common.base.UiState

data class CalendarState(
    val personalSchedules: List<ScheduleUiModel> = emptyList(),
    val isLoading: Boolean = false,
) : UiState
