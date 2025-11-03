package pusan.university.plato_calendar.presentation.setting.intent

import pusan.university.plato_calendar.presentation.common.base.UiState
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime

data class SettingState(
    val userInfo: String? = null,
    val isLoginDialogVisible: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val firstReminderTime: NotificationTime = NotificationTime.ONE_HOUR,
    val secondReminderTime: NotificationTime = NotificationTime.NONE,
    val isNotificationPermissionSettingsDialogVisible: Boolean = false,
    val shouldPromptNotificationPermission: Boolean = false,
    val hasNotificationPermission: Boolean = false,
) : UiState
