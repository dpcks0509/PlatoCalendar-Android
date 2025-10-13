package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.common.base.UiSideEffect

sealed interface CalendarSideEffect : UiSideEffect {
    data object HideScheduleBottomSheet : CalendarSideEffect

    data class ScrollToPage(val page: Int) : CalendarSideEffect
}
