package pnu.plato.calendar.presentation.common.navigation

import kotlinx.serialization.Serializable

sealed interface PlatoCalendarScreen {
    @Serializable
    data object CalendarScreen : PlatoCalendarScreen

    @Serializable
    data object ToDoScreen : PlatoCalendarScreen

    @Serializable
    data object SettingScreen : PlatoCalendarScreen
}