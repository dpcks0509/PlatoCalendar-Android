package pusan.university.plato_calendar.presentation.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.domain.repository.CourseRepository
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.common.manager.LoginManager
import pusan.university.plato_calendar.presentation.common.manager.ScheduleManager
import pusan.university.plato_calendar.presentation.common.manager.SettingsManager
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Suppress("RestrictedApi")
object CalendarWidget : GlanceAppWidget() {

    internal val scheduleCountKey = intPreferencesKey("schedule_count")
    internal val lastUpdateKey = stringPreferencesKey("last_update")

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetEntryPoint {
        fun scheduleRepository(): ScheduleRepository
        fun courseRepository(): CourseRepository
        fun loginManager(): LoginManager
        fun scheduleManager(): ScheduleManager
        fun settingsManager(): SettingsManager
        fun alarmScheduler(): AlarmScheduler
    }

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
            val scheduleCount = prefs[scheduleCountKey] ?: 0
            val lastUpdate = prefs[lastUpdateKey] ?: "아직 업데이트 안됨"

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Text(
                    text = "PLATO 캘린더",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = ColorProvider(Color.Black)
                    )
                )

                Spacer(modifier = GlanceModifier.height(16.dp))

                Text(
                    text = "일정 개수: $scheduleCount",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = ColorProvider(Color.Black)
                    )
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                Text(
                    text = "마지막 업데이트:",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(Color.Gray)
                    )
                )

                Text(
                    text = lastUpdate,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(Color.Gray)
                    )
                )

                Spacer(modifier = GlanceModifier.height(24.dp))

                Button(
                    text = "🔄 새로고침",
                    onClick = actionRunCallback<RefreshSchedulesCallback>()
                )
            }
        }
    }
}

class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = CalendarWidget
}

class RefreshSchedulesCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Hilt EntryPoint를 통해 의존성 가져오기
        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            CalendarWidget.WidgetEntryPoint::class.java
        )

        val scheduleRepository = entryPoint.scheduleRepository()
        val courseRepository = entryPoint.courseRepository()
        val loginManager = entryPoint.loginManager()
        val scheduleManager = entryPoint.scheduleManager()
        val settingsManager = entryPoint.settingsManager()
        val alarmScheduler = entryPoint.alarmScheduler()

        loginManager.autoLogin()

        // 일정 가져오기 (CalendarViewModel의 getSchedules() 로직과 동일)
        val (scheduleCount, allSchedules) = withContext(Dispatchers.IO) {
            when (val loginStatus = loginManager.loginStatus.value) {
                is LoginStatus.Login -> {
                    val (academicSchedules, personalSchedules) = awaitAll(
                        async { getAcademicSchedules(scheduleRepository) },
                        async {
                            getPersonalSchedules(
                                scheduleRepository,
                                courseRepository,
                                loginStatus.loginSession.sessKey
                            )
                        }
                    )
                    val allSchedules = academicSchedules + personalSchedules
                    Pair(allSchedules.size, allSchedules)
                }

                LoginStatus.Logout, LoginStatus.Uninitialized, LoginStatus.NetworkDisconnected -> {
                    val academicSchedules = getAcademicSchedules(scheduleRepository)
                    Pair(academicSchedules.size, academicSchedules)
                }
            }
        }

        // ScheduleManager 업데이트
        scheduleManager.updateSchedules(allSchedules)

        // 알람 동기화 (NotificationSyncManager와 동일한 로직)
        syncAlarms(allSchedules, settingsManager, alarmScheduler)

        // 위젯 상태 업데이트
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[CalendarWidget.scheduleCountKey] = scheduleCount
            prefs[CalendarWidget.lastUpdateKey] = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

        // 위젯 UI 업데이트
        CalendarWidget.update(context, glanceId)
    }

    private suspend fun syncAlarms(
        schedules: List<ScheduleUiModel>,
        settingsManager: SettingsManager,
        alarmScheduler: AlarmScheduler
    ) {
        val settings = settingsManager.appSettings.first()

        // 완료되지 않은 개인 일정만 필터링
        val personalSchedules = schedules
            .filterIsInstance<PersonalScheduleUiModel>()
            .filter { !it.isCompleted }

        // 기존 알람 모두 취소
        alarmScheduler.cancelAllNotifications()

        // 알림이 활성화되어 있으면 새로 등록
        if (settings.notificationsEnabled) {
            alarmScheduler.scheduleNotificationsForSchedule(
                personalSchedules = personalSchedules,
                firstReminderTime = settings.firstReminderTime,
                secondReminderTime = settings.secondReminderTime
            )
        }
    }

    private suspend fun getAcademicSchedules(
        scheduleRepository: ScheduleRepository
    ): List<AcademicScheduleUiModel> {
        return scheduleRepository.getAcademicSchedules()
            .getOrNull()
            ?.map { AcademicScheduleUiModel(it) }
            ?: emptyList()
    }

    private suspend fun getPersonalSchedules(
        scheduleRepository: ScheduleRepository,
        courseRepository: CourseRepository,
        sessKey: String
    ): List<PersonalScheduleUiModel> {
        return scheduleRepository.getPersonalSchedules(sessKey)
            .getOrNull()
            ?.map { domain ->
                when (domain) {
                    is CourseSchedule -> {
                        val courseName = courseRepository.getCourseName(domain.courseCode)
                        CourseScheduleUiModel(domain = domain, courseName = courseName)
                    }

                    is CustomSchedule -> CustomScheduleUiModel(domain)
                }
            }
            ?: emptyList()
    }
}