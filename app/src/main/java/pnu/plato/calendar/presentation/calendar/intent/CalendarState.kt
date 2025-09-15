package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.calendar.model.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.common.base.UiState
import java.time.LocalDate

data class CalendarState(
    val today: LocalDate = LocalDate.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val academicSchedules: List<AcademicScheduleUiModel> = emptyList(),
    val personalSchedules: List<PersonalScheduleUiModel> = emptyList(),
) : UiState
