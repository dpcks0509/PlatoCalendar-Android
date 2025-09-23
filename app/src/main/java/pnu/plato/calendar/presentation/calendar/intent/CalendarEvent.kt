package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.base.UiEvent
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface CalendarEvent : UiEvent {
    data object MoveToToday : CalendarEvent

    data class MakeCustomSchedule(
        val title: String,
        val description: String?,
        val startAt: LocalDateTime,
        val endAt: LocalDateTime,
    ) : CalendarEvent

    data class UpdateSelectedDate(
        val date: LocalDate,
    ) : CalendarEvent

    data class UpdateSelectedSchedule(
        val schedule: ScheduleUiModel?,
    ) : CalendarEvent

    data class UpdateCurrentYearMonth(
        val yearMonth: YearMonth,
    ) : CalendarEvent

    data object UpdateSchedules : CalendarEvent

    data class ShowScheduleBottomSheet(
        val schedule: ScheduleUiModel?,
    ) : CalendarEvent

    data object HideScheduleBottomSheet : CalendarEvent
}
