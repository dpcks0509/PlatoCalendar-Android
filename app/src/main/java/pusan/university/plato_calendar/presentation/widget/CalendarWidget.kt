package pusan.university.plato_calendar.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pusan.university.plato_calendar.domain.repository.CourseRepository
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.common.extension.formatTimeWithMidnightSpecialCase
import pusan.university.plato_calendar.presentation.common.manager.LoginManager
import pusan.university.plato_calendar.presentation.common.manager.ScheduleManager
import pusan.university.plato_calendar.presentation.common.manager.SettingsManager
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler
import pusan.university.plato_calendar.presentation.common.serializer.PersonalScheduleSerializer.deserializePersonalSchedules
import pusan.university.plato_calendar.presentation.widget.callback.NavigateDateCallback
import pusan.university.plato_calendar.presentation.widget.callback.OpenNewScheduleCallback
import pusan.university.plato_calendar.presentation.widget.callback.RefreshSchedulesCallback
import java.time.LocalDate
import java.time.format.TextStyle.FULL
import java.util.Locale.KOREAN

@Suppress("RestrictedApi")
object CalendarWidget : GlanceAppWidget() {
    init {
        actionRunCallback<RefreshSchedulesCallback>()
    }

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

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            val prefs = currentState<Preferences>()
            val schedulesJson = prefs[stringPreferencesKey("schedules_list")] ?: ""
            val todayStr = prefs[stringPreferencesKey("today")] ?: LocalDate.now().toString()
            val selectedDateStr = prefs[stringPreferencesKey("selected_date")] ?: todayStr

            val today = LocalDate.parse(todayStr)
            val selectedDate = LocalDate.parse(selectedDateStr)

            val schedules = deserializePersonalSchedules(schedulesJson)
            val schedulesMap = schedules.groupBy { schedule -> schedule.endAt.toLocalDate() }
            val selectedDateSchedules =
                schedulesMap[selectedDate]?.filter { !it.isCompleted } ?: emptyList()

            Column(
                modifier =
                    GlanceModifier
                        .fillMaxSize()
                        .appWidgetBackground()
                        .cornerRadius(16.dp)
                        .background(Color.White)
                        .padding(16.dp),
                verticalAlignment = Alignment.Vertical.Top,
            ) {
                // 상단: 날짜와 버튼들
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.Start,
                ) {
                    // 날짜 표시
                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        verticalAlignment = Alignment.Vertical.Top,
                    ) {
                        Text(
                            text = selectedDate.dayOfMonth.toString().padStart(2, '0'),
                            style =
                                TextStyle(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorProvider(Color.Black),
                                ),
                        )
                        Text(
                            text =
                                selectedDate.dayOfWeek.getDisplayName(FULL, KOREAN),
                            style =
                                TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = ColorProvider(Color.Gray),
                                ),
                        )
                    }

                    // 네비게이션 버튼들
                    Row(
                        horizontalAlignment = Alignment.Horizontal.End,
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                    ) {
                        // 이전 날짜 버튼
                        Button(
                            text = "<",
                            onClick =
                                actionRunCallback<NavigateDateCallback>(
                                    actionParametersOf(
                                        NavigateDateCallback.currentDateKey to selectedDate.toString(),
                                        NavigateDateCallback.offsetKey to "-1",
                                    ),
                                ),
                            modifier =
                                GlanceModifier
                                    .width(40.dp)
                                    .height(40.dp),
                        )

                        Spacer(modifier = GlanceModifier.width(8.dp))

                        // 다음 날짜 버튼
                        Button(
                            text = ">",
                            onClick =
                                actionRunCallback<NavigateDateCallback>(
                                    actionParametersOf(
                                        NavigateDateCallback.currentDateKey to selectedDate.toString(),
                                        NavigateDateCallback.offsetKey to "1",
                                    ),
                                ),
                            modifier =
                                GlanceModifier
                                    .width(40.dp)
                                    .height(40.dp),
                        )

                        Spacer(modifier = GlanceModifier.width(8.dp))

                        // 새로고침 버튼
                        Button(
                            text = "+",
                            onClick = actionStartActivity(
                                Intent().apply {
                                    setClassName(
                                        "pusan.university.plato_calendar",
                                        "pusan.university.plato_calendar.presentation.PlatoCalendarActivity"
                                    )
                                    action = OpenNewScheduleCallback.ACTION_OPEN_NEW_SCHEDULE
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                }
                            ),
                            modifier =
                                GlanceModifier
                                    .width(40.dp)
                                    .height(40.dp),
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(16.dp))

                // 일정 목록 (스크롤 가능)
                LazyColumn(
                    modifier =
                        GlanceModifier
                            .fillMaxWidth()
                            .defaultWeight(),
                ) {
                    if (selectedDateSchedules.isEmpty()) {
                        item {
                            Text(
                                text = "일정 없음",
                                style =
                                    TextStyle(
                                        fontSize = 14.sp,
                                        color = ColorProvider(Color.Gray),
                                    ),
                                modifier = GlanceModifier.padding(vertical = 8.dp),
                            )
                        }
                    } else {
                        items(selectedDateSchedules) { schedule ->
                            ScheduleWidgetItem(schedule)
                            Spacer(modifier = GlanceModifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    data class ScheduleWidgetUiModel(
        val title: String,
        val deadLine: String,
        val color: Color
    )

    @Composable
    private fun ScheduleWidgetItem(schedule: PersonalScheduleUiModel) {
        val scheduleWidgetItem =
            when (schedule) {
                is CourseScheduleUiModel ->
                    ScheduleWidgetUiModel(
                        schedule.courseName.ifEmpty { schedule.title },
                        schedule.endAt.formatTimeWithMidnightSpecialCase() + " 까지",
                        if (schedule.isCompleted) Color.Gray else Color(0xFF33B679),
                    )

                is CustomScheduleUiModel ->
                    ScheduleWidgetUiModel(
                        schedule.title,
                        schedule.endAt.formatTimeWithMidnightSpecialCase() + " 까지",
                        if (schedule.isCompleted) Color.Gray else Color(0xFFE67C73),
                    )
            }

        val intent = Intent().apply {
            setClassName(
                "pusan.university.plato_calendar",
                "pusan.university.plato_calendar.presentation.PlatoCalendarActivity"
            )
            putExtra(AlarmScheduler.EXTRA_SCHEDULE_ID, schedule.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(actionStartActivity(intent)),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Spacer(
                modifier =
                    GlanceModifier
                        .width(8.dp)
                        .height(8.dp)
                        .background(scheduleWidgetItem.color)
                        .cornerRadius(4.dp),
            )

            Spacer(modifier = GlanceModifier.width(12.dp))

            Column(
                modifier = GlanceModifier.defaultWeight(),
            ) {
                Text(
                    text = scheduleWidgetItem.title,
                    style =
                        TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorProvider(Color.Black),
                        ),
                    maxLines = 2,
                )

                Spacer(modifier = GlanceModifier.height(2.dp))

                Text(
                    text = scheduleWidgetItem.deadLine,
                    style =
                        TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = ColorProvider(Color.Gray),
                        ),
                )
            }
        }
    }
}
