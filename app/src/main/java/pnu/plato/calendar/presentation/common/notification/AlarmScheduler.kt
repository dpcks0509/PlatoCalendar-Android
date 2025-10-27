package pnu.plato.calendar.presentation.common.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.common.manager.ScheduleManager
import pnu.plato.calendar.presentation.setting.model.NotificationTime
import pnu.plato.calendar.presentation.setting.model.NotificationTime.Companion.getReminderTime
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(@ApplicationContext private val context: Context) {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var scheduleManager: ScheduleManager

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun scheduleNotificationsForSchedule(
        personalSchedules: List<PersonalScheduleUiModel>,
        firstReminderTime: NotificationTime,
        secondReminderTime: NotificationTime
    ) {
        personalSchedules.forEach { personalSchedule ->
            val now = LocalDateTime.now()

            scheduleReminderIfNeeded(
                personalSchedule = personalSchedule,
                now = now,
                reminderTime = firstReminderTime,
                reminderIndex = 1
            )

            scheduleReminderIfNeeded(
                personalSchedule = personalSchedule,
                now = now,
                reminderTime = secondReminderTime,
                reminderIndex = 2
            )
        }
    }

    private fun scheduleReminderIfNeeded(
        personalSchedule: PersonalScheduleUiModel,
        now: LocalDateTime,
        reminderTime: NotificationTime,
        reminderIndex: Int
    ) {
        if (reminderTime != NotificationTime.NONE) {
            val reminderDateTime = calculateReminderTime(personalSchedule.endAt, reminderTime)
            if (reminderDateTime.isAfter(now)) {
                val notificationId = generateNotificationId(personalSchedule.id, reminderIndex)
                scheduleNotificationWithScheduleId(
                    notificationId = notificationId,
                    scheduleId = personalSchedule.id,
                    title = personalSchedule.title,
                    message = if (!personalSchedule.description.isNullOrBlank()) {
                        "${personalSchedule.description} · ${reminderTime.getReminderTime()}"
                    } else {
                        "· ${reminderTime.getReminderTime()}"
                    },
                    calendar = localDateTimeToCalendar(reminderDateTime)
                )
            }
        }
    }

    private fun scheduleNotificationWithScheduleId(
        notificationId: Int,
        scheduleId: Long,
        title: String,
        message: String,
        calendar: Calendar
    ) {
        val triggerTime = calendar.timeInMillis

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(AlarmReceiver.EXTRA_TITLE, title)
            putExtra(AlarmReceiver.EXTRA_MESSAGE, message)
            putExtra(AlarmReceiver.EXTRA_SCHEDULE_ID, scheduleId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    fun cancelNotificationsForSchedule(scheduleId: Long) {
        cancelNotification(generateNotificationId(scheduleId, 1))
        cancelNotification(generateNotificationId(scheduleId, 2))
    }

    fun cancelNotification(notificationId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        notificationHelper.cancelNotification(notificationId)
    }

    fun cancelAllNotifications() {
        val personalSchedules = scheduleManager.schedules.value
            .filterIsInstance<PersonalScheduleUiModel>()

        personalSchedules.forEach { schedule ->
            cancelNotification(generateNotificationId(schedule.id, 1))
            cancelNotification(generateNotificationId(schedule.id, 2))
        }

        notificationHelper.cancelAllNotifications()
    }

    private fun calculateReminderTime(
        endDateTime: LocalDateTime,
        reminderTime: NotificationTime
    ): LocalDateTime {
        return when (reminderTime) {
            NotificationTime.ONE_HOUR -> endDateTime.minusHours(1)
            NotificationTime.TWO_HOURS -> endDateTime.minusHours(2)
            NotificationTime.SIX_HOURS -> endDateTime.minusHours(6)
            NotificationTime.TWELVE_HOURS -> endDateTime.minusHours(12)
            NotificationTime.ONE_DAY -> endDateTime.minusDays(1)
            NotificationTime.TWO_DAYS -> endDateTime.minusDays(2)
            NotificationTime.ONE_WEEK -> endDateTime.minusWeeks(1)
            else -> endDateTime
        }
    }

    private fun localDateTimeToCalendar(localDateTime: LocalDateTime): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }

    private fun generateNotificationId(scheduleId: Long, reminderIndex: Int): Int {
        return (scheduleId * 10 + reminderIndex).toInt()
    }
}