package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.base.UiEvent
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface CalendarEvent : UiEvent {
    data object MoveToToday : CalendarEvent

    data class MakePersonalSchedule(
        val title: String,
        val description: String?,
        val startAt: LocalDateTime,
        val endAt: LocalDateTime,
    ) : CalendarEvent

    data class ChangeSelectedDate(
        val date: LocalDate,
    ) : CalendarEvent

    data class ChangeCurrentYearMonth(
        val yearMonth: YearMonth,
    ) : CalendarEvent

    data object RefreshSchedules : CalendarEvent
}
