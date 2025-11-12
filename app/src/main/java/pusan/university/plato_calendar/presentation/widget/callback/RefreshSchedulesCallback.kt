package pusan.university.plato_calendar.presentation.widget.callback

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.common.eventbus.ToastEventBus
import pusan.university.plato_calendar.presentation.common.serializer.ScheduleSerializer
import pusan.university.plato_calendar.presentation.widget.CalendarWidget
import pusan.university.plato_calendar.presentation.widget.CalendarWidget.WidgetEntryPoint
import java.time.LocalDate

class RefreshSchedulesCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val entryPoint =
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                WidgetEntryPoint::class.java,
            )

        val scheduleRepository = entryPoint.scheduleRepository()
        val courseRepository = entryPoint.courseRepository()
        val loginManager = entryPoint.loginManager()
        val scheduleManager = entryPoint.scheduleManager()
        val settingsManager = entryPoint.settingsManager()
        val alarmScheduler = entryPoint.alarmScheduler()

        suspend fun getAcademicSchedules(): List<AcademicScheduleUiModel> {
            scheduleRepository
                .getAcademicSchedules()
                .onSuccess {
                    val academicSchedules = it.map(::AcademicScheduleUiModel)

                    return academicSchedules
                }.onFailure { throwable ->
                    if (throwable !is NoNetworkConnectivityException) {
                        ToastEventBus.sendError(
                            throwable.message,
                        )
                    }
                }

            return emptyList()
        }

        suspend fun getPersonalSchedules(sessKey: String): List<PersonalScheduleUiModel> {
            scheduleRepository
                .getPersonalSchedules(sessKey = sessKey)
                .onSuccess {
                    val personalSchedules =
                        it.map { domain ->
                            when (domain) {
                                is CourseSchedule -> {
                                    val courseName =
                                        courseRepository.getCourseName(
                                            domain.courseCode,
                                        )

                                    CourseScheduleUiModel(
                                        domain = domain,
                                        courseName = courseName,
                                    )
                                }

                                is CustomSchedule -> CustomScheduleUiModel(domain)
                            }
                        }

                    return personalSchedules
                }.onFailure { throwable ->
                    scheduleManager.updateLoading(false)
                }

            return emptyList()
        }

        suspend fun syncAlarms(schedules: List<ScheduleUiModel>) {
            val settings = settingsManager.appSettings.first()

            val personalSchedules =
                schedules
                    .filterIsInstance<PersonalScheduleUiModel>()
                    .filter { !it.isCompleted }

            alarmScheduler.cancelAllNotifications()

            if (settings.notificationsEnabled) {
                alarmScheduler.scheduleNotificationsForSchedule(
                    personalSchedules = personalSchedules,
                    firstReminderTime = settings.firstReminderTime,
                    secondReminderTime = settings.secondReminderTime,
                )
            }
        }

        loginManager.autoLogin()

        val schedules =
            withContext(Dispatchers.IO) {
                when (val loginStatus = loginManager.loginStatus.value) {
                    is LoginStatus.Login -> {
                        scheduleManager.updateLoading(true)

                        val (academicSchedules, personalSchedules) =
                            awaitAll(
                                async { getAcademicSchedules() },
                                async { getPersonalSchedules(loginStatus.loginSession.sessKey) },
                            )

                        val schedules = academicSchedules + personalSchedules

                        if (schedules.isNotEmpty()) scheduleManager.updateSchedules(schedules)
                        scheduleManager.updateLoading(false)
                        schedules
                    }

                    LoginStatus.Logout, LoginStatus.Uninitialized, LoginStatus.NetworkDisconnected -> {
                        val academicSchedules = getAcademicSchedules()
                        scheduleManager.updateLoading(false)
                        academicSchedules
                    }
                }
            }

        syncAlarms(schedules)

        val schedulesJson = ScheduleSerializer.serializeSchedules(schedules)
        val today = LocalDate.now().toString()

        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[stringPreferencesKey("schedules_list")] = schedulesJson
            prefs[stringPreferencesKey("today")] = today
        }

        CalendarWidget.update(context, glanceId)
    }
}
