package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.calendar.model.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.common.base.UiState

data class CalendarState(
    val personalSchedules: List<PersonalScheduleUiModel> = emptyList(),
    val academicSchedules: List<AcademicScheduleUiModel> = emptyList(),
    val isLoading: Boolean = false,
) : UiState
