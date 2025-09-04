package pusan.university.plato_calendar.presentation.calendar

import dagger.hilt.android.lifecycle.HiltViewModel
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarSideEffect
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarState
import pusan.university.plato_calendar.presentation.common.base.BaseViewModel
import pusan.university.plato_calendar.presentation.common.manager.LoginManager
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val userManager: LoginManager
) : BaseViewModel<CalendarState, CalendarEvent, CalendarSideEffect>(initialState = CalendarState()) {

    override suspend fun handleEvent(event: CalendarEvent) {
        when (event) {
            else -> {}
        }
    }
}