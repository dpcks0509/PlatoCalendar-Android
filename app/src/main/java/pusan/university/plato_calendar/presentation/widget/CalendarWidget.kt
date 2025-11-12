package pusan.university.plato_calendar.presentation.widget

import android.content.Context
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
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
import pusan.university.plato_calendar.presentation.common.serializer.ScheduleSerializer
import pusan.university.plato_calendar.presentation.widget.callback.NavigateDateCallback
import pusan.university.plato_calendar.presentation.widget.callback.OpenNewScheduleCallback
import pusan.university.plato_calendar.presentation.widget.callback.RefreshSchedulesCallback
import pusan.university.plato_calendar.presentation.widget.callback.OpenScheduleDetailCallback
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

            val schedules = ScheduleSerializer.deserializeSchedules(schedulesJson)
            val schedulesMap = groupSchedulesByDate(schedules)
            val selectedDateSchedules = schedulesMap[selectedDate] ?: emptyList()

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
                                    color = androidx.glance.unit.ColorProvider(Color.Black),
                                ),
                        )
                        Text(
                            text =
                                selectedDate.dayOfWeek.getDisplayName(
                                    java.time.format.TextStyle.FULL,
                                    java.util.Locale.KOREAN,
                                ),
                            style =
                                TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = androidx.glance.unit.ColorProvider(Color.Gray),
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
                            onClick = actionRunCallback<OpenNewScheduleCallback>(),
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
                                        color = androidx.glance.unit.ColorProvider(Color.Gray),
                                    ),
                                modifier = GlanceModifier.padding(vertical = 8.dp),
                            )
                        }
                    } else {
                        items(selectedDateSchedules) { schedule ->
                            ScheduleItem(schedule)
                            Spacer(modifier = GlanceModifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ScheduleItem(schedule: ScheduleUiModel) {
        val (title, timeRange, color) =
            when (schedule) {
                is AcademicScheduleUiModel -> Triple(schedule.title, "", Color(0xFF9575CD))
                is CourseScheduleUiModel ->
                    Triple(
                        schedule.courseName.ifEmpty { schedule.title },
                        "${
                            schedule.startAt.format(
                                DateTimeFormatter.ofPattern("HH:mm"),
                            )
                        } - ${schedule.endAt.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        if (schedule.isCompleted) Color.Gray else Color(0xFF4285F4),
                    )

                is CustomScheduleUiModel ->
                    Triple(
                        schedule.title,
                        "${
                            schedule.startAt.format(
                                DateTimeFormatter.ofPattern("HH:mm"),
                            )
                        } - ${schedule.endAt.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        if (schedule.isCompleted) Color.Gray else Color(0xFF4285F4),
                    )
            }

        val modifier =
            if (schedule is PersonalScheduleUiModel) {
                GlanceModifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(
                        actionRunCallback<OpenScheduleDetailCallback>(
                            actionParametersOf(
                                OpenScheduleDetailCallback.scheduleIdKey to schedule.id,
                            ),
                        ),
                    )
            } else {
                GlanceModifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            }

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            // 원형 인디케이터
            Spacer(
                modifier =
                    GlanceModifier
                        .width(8.dp)
                        .height(8.dp)
                        .background(color)
                        .cornerRadius(4.dp),
            )

            Spacer(modifier = GlanceModifier.width(12.dp))

            Column(
                modifier = GlanceModifier.defaultWeight(),
            ) {
                Text(
                    text = title,
                    style =
                        TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = androidx.glance.unit.ColorProvider(Color.Black),
                        ),
                    maxLines = 2,
                )
                if (timeRange.isNotEmpty()) {
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = timeRange,
                        style =
                            TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = androidx.glance.unit.ColorProvider(Color.Gray),
                            ),
                    )
                }
            }
        }
    }

    private fun groupSchedulesByDate(schedules: List<ScheduleUiModel>): Map<LocalDate, List<ScheduleUiModel>> =
        schedules.groupBy { schedule ->
            when (schedule) {
                is AcademicScheduleUiModel -> schedule.endAt
                is PersonalScheduleUiModel -> schedule.endAt.toLocalDate()
            }
        }
}
