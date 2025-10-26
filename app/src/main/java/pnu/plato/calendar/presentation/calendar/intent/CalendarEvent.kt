package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.domain.entity.LoginCredentials
import pnu.plato.calendar.domain.entity.Schedule.NewSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.base.UiEvent
import java.time.LocalDate

sealed interface CalendarEvent : UiEvent {
    data object MoveToToday : CalendarEvent

    data object Refresh : CalendarEvent

    data class MakeCustomSchedule(
        val schedule: NewSchedule,
    ) : CalendarEvent

    data class TryLogin(
        val loginCredentials: LoginCredentials,
    ) : CalendarEvent

    data class EditCustomSchedule(
        val schedule: CustomSchedule,
    ) : CalendarEvent

    data class DeleteCustomSchedule(
        val id: Long,
    ) : CalendarEvent

    data class UpdateSelectedDate(
        val date: LocalDate,
    ) : CalendarEvent

    data class UpdateCurrentYearMonth(
        val yearMonth: YearMonth,
    ) : CalendarEvent

    data class TogglePersonalScheduleCompletion(
        val id: Long,
        val isCompleted: Boolean,
    ) : CalendarEvent

    data class ShowScheduleBottomSheet(
        val schedule: ScheduleUiModel? = null,
    ) : CalendarEvent

    data class ShowScheduleBottomSheetById(
        val scheduleId: Long,
    ) : CalendarEvent

    data object HideScheduleBottomSheet : CalendarEvent

    data object HideLoginDialog : CalendarEvent
}
