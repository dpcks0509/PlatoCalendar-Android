package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.common.base.UiEvent
import java.time.LocalDateTime

sealed interface CalendarEvent : UiEvent {
    data object GetPersonalSchedules : CalendarEvent

    data object MoveToToday : CalendarEvent

    data class MakePersonalSchedule(
        val title: String,
        val description: String?,
        val startAt: LocalDateTime,
        val endAt: LocalDateTime,
    ) : CalendarEvent
}
