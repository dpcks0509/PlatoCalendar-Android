package pnu.plato.calendar.presentation.common.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.common.notification.AlarmScheduler
import pnu.plato.calendar.presentation.setting.model.NotificationTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSyncManager
@Inject
constructor(
    private val scheduleManager: ScheduleManager,
    private val settingsManager: SettingsManager,
    private val alarmScheduler: AlarmScheduler,
) {
    private var syncJob: Job? = null

    fun startSync(scope: CoroutineScope) {
        syncJob?.cancel()

        syncJob = scope.launch {
            combine(
                scheduleManager.schedules,
                settingsManager.appSettings
            ) { schedules, settings ->
                Pair(schedules, settings)
            }.collect { (schedules, settings) ->
                val personalSchedules =
                    schedules.filterIsInstance<PersonalScheduleUiModel>().filter { !it.isCompleted }

                syncNotifications(
                    personalSchedules = personalSchedules,
                    notificationsEnabled = settings.notificationsEnabled,
                    firstReminderTime = settings.firstReminderTime,
                    secondReminderTime = settings.secondReminderTime
                )
            }
        }
    }

    private fun syncNotifications(
        personalSchedules: List<PersonalScheduleUiModel>,
        notificationsEnabled: Boolean,
        firstReminderTime: NotificationTime,
        secondReminderTime: NotificationTime
    ) {
        if (!notificationsEnabled) {
            alarmScheduler.cancelAllNotifications()
            return
        }

        alarmScheduler.cancelAllNotifications()
        alarmScheduler.scheduleNotificationsForSchedule(
            personalSchedules = personalSchedules,
            firstReminderTime = firstReminderTime,
            secondReminderTime = secondReminderTime
        )
    }
}
