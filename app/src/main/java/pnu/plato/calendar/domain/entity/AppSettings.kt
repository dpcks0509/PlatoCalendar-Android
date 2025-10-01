package pnu.plato.calendar.domain.entity

import pnu.plato.calendar.presentation.setting.model.NotificationTime

data class AppSettings(
    val notificationsEnabled: Boolean = false,
    val academicScheduleEnabled: Boolean = false,
    val firstReminderTime: NotificationTime = NotificationTime.ONE_HOUR,
    val secondReminderTime: NotificationTime = NotificationTime.NONE,
)
