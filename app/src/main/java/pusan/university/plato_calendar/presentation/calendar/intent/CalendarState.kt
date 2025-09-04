package pusan.university.plato_calendar.presentation.calendar.intent

import pusan.university.plato_calendar.domain.entity.Schedule
import pusan.university.plato_calendar.presentation.common.base.UiState

data class CalendarState(
    val schedules: List<Schedule> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
): UiState