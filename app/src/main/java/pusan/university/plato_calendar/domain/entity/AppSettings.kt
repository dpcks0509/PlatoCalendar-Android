package pusan.university.plato_calendar.domain.entity

import pusan.university.plato_calendar.presentation.setting.model.NotificationTime

data class AppSettings(
    val notificationsEnabled: Boolean = false,
    val firstReminderTime: NotificationTime = NotificationTime.ONE_DAY,
    val secondReminderTime: NotificationTime = NotificationTime.NONE,
)
