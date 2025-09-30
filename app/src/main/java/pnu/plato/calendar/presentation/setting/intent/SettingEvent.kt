package pnu.plato.calendar.presentation.setting.intent

import pnu.plato.calendar.presentation.common.base.UiEvent

sealed interface SettingEvent : UiEvent {
    data object ShowLoginDialog : SettingEvent

    data object HideLoginDialog : SettingEvent

    data object Logout : SettingEvent
}
