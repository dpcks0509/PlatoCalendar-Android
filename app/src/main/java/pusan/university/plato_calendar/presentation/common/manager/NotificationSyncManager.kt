package pusan.university.plato_calendar.presentation.common.manager

import android.content.Context
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidgetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler
import pusan.university.plato_calendar.presentation.widget.CalendarWidget
import pusan.university.plato_calendar.presentation.widget.callback.RefreshSchedulesCallback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSyncManager
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val scheduleManager: ScheduleManager,
    private val settingsManager: SettingsManager,
    private val alarmScheduler: AlarmScheduler,
) {
    private var syncJob: Job? = null

    fun startSync(scope: CoroutineScope) {
        syncJob?.cancel()

        syncJob =
            scope.launch {
                combine(
                    scheduleManager.schedules,
                    settingsManager.appSettings,
                ) { schedules, settings ->
                    Pair(schedules, settings)
                }.collect { (schedules, settings) ->
                    val personalSchedules =
                        schedules.filterIsInstance<PersonalScheduleUiModel>()
                            .filter { !it.isCompleted }

                    val glanceManager = GlanceAppWidgetManager(context)
                    val glanceIds = glanceManager.getGlanceIds(CalendarWidget::class.java)
                    glanceIds.forEach { glanceId ->
                        RefreshSchedulesCallback().onAction(
                            context = context,
                            glanceId = glanceId,
                            parameters = actionParametersOf(),
                        )
                    }

                    with(settings) {
                        alarmScheduler.cancelAllNotifications()

                        if (notificationsEnabled) {
                            alarmScheduler.scheduleNotificationsForSchedule(
                                personalSchedules = personalSchedules,
                                firstReminderTime = firstReminderTime,
                                secondReminderTime = secondReminderTime,
                            )
                        }
                    }
                }
            }
    }
}
