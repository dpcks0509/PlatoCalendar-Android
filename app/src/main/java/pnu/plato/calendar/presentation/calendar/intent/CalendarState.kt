package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.base.UiState
import java.time.LocalDate

data class CalendarState(
    val selectedDate: LocalDate = today,
    val selectedSchedule: ScheduleUiModel? = null,
    val currentYearMonth: YearMonth = YearMonth(year = today.year, month = today.monthValue),
    val schedules: List<ScheduleUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isScheduleBottomSheetVisible: Boolean = false,
) : UiState {
    val selectedDateSchedules: List<ScheduleUiModel>
        get() =
            schedules.filter { schedule ->
                when (schedule) {
                    is AcademicScheduleUiModel -> schedule.endAt == selectedDate
                    is PersonalScheduleUiModel -> schedule.endAt.toLocalDate() == selectedDate
                }
            }
}
