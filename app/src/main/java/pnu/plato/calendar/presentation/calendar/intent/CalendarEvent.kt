package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.common.base.UiEvent

sealed interface CalendarEvent : UiEvent {
    data object FetchPersonalSchedules : CalendarEvent
}
