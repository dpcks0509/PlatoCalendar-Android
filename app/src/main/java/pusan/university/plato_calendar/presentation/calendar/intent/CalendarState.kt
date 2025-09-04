package pusan.university.plato_calendar.presentation.calendar.intent

import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.common.base.UiState

data class CalendarState(
    val schedules: List<ScheduleUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState