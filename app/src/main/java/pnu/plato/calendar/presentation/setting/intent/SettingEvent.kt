package pnu.plato.calendar.presentation.setting.intent

import pnu.plato.calendar.presentation.common.base.UiEvent
import pnu.plato.calendar.presentation.setting.model.NotificationTime

sealed interface SettingEvent : UiEvent {
    data object ShowLoginDialog : SettingEvent

    data object HideLoginDialog : SettingEvent

    data object Logout : SettingEvent

    data class SetNotificationsEnabled(
        val enabled: Boolean,
    ) : SettingEvent

    data class SetAcademicScheduleEnabled(
        val enabled: Boolean,
    ) : SettingEvent

    data class SetFirstReminderTime(
        val time: NotificationTime,
    ) : SettingEvent

    data class SetSecondReminderTime(
        val time: NotificationTime,
    ) : SettingEvent

    data class NavigateToWebView(
        val url: String,
    ) : SettingEvent
}
