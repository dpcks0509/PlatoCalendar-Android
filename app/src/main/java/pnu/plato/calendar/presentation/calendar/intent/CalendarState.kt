package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.calendar.model.StudentScheduleUiModel
import pnu.plato.calendar.presentation.common.base.UiState

data class CalendarState(
    val schedules: List<StudentScheduleUiModel> = emptyList(),
    val isLoading: Boolean = false,
) : UiState
