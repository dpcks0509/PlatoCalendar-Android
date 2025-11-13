package pusan.university.plato_calendar.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
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
import pusan.university.plato_calendar.R
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle.FULL
import java.time.format.TextStyle.SHORT
import java.time.temporal.TemporalAdjusters
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

            // 이번 주의 월요일 구하기
            val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val weekDates = (0..6).map { monday.plusDays(it.toLong()) }

            val schedules = deserializePersonalSchedules(schedulesJson)
            val schedulesMap = schedules.groupBy { schedule -> schedule.endAt.toLocalDate() }
            val selectedDateSchedules =
                schedulesMap[selectedDate]?.filter { !it.isCompleted } ?: emptyList()

            Column(
                modifier =
                    GlanceModifier
                        .fillMaxSize()
                        .background(ImageProvider(R.drawable.widget_background))
                        .padding(16.dp),
                verticalAlignment = Alignment.Vertical.Top,
            ) {
                // 상단: 날짜와 + 버튼
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.Start,
                ) {
                    // 날짜 표시
                    Row(
                        modifier = GlanceModifier.defaultWeight(),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.Start,
                    ) {
                        // 날짜 숫자 (왼쪽)
                        Text(
                            text = selectedDate.dayOfMonth.toString(),
                            style =
                                TextStyle(
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorProvider(Color.Black),
                                ),
                        )

                        Spacer(modifier = GlanceModifier.width(12.dp))

                        // 월과 요일 (오른쪽)
                        Column(
                            verticalAlignment = Alignment.Vertical.CenterVertically,
                        ) {
                            Text(
                                text = "${selectedDate.monthValue}월, ${selectedDate.year}",
                                style =
                                    TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ColorProvider(Color.Black),
                                    ),
                            )
                            Text(
                                text =
                                    if (selectedDate == today) {
                                        "오늘, ${selectedDate.dayOfWeek.getDisplayName(FULL, KOREAN)}"
                                    } else {
                                        selectedDate.dayOfWeek.getDisplayName(FULL, KOREAN)
                                    },
                                style =
                                    TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = ColorProvider(Color.Gray),
                                    ),
                            )
                        }
                    }

                    // + 버튼
                    Box(
                        modifier =
                            GlanceModifier
                                .width(36.dp)
                                .height(36.dp)
                                .background(ImageProvider(R.drawable.widget_circle_blue))
                                .clickable(
                                    actionStartActivity(
                                        Intent().apply {
                                            setClassName(
                                                "pusan.university.plato_calendar",
                                                "pusan.university.plato_calendar.presentation.PlatoCalendarActivity",
                                            )
                                            action = OpenNewScheduleCallback.ACTION_OPEN_NEW_SCHEDULE
                                            flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                        },
                                    ),
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "+",
                            style =
                                TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ColorProvider(Color.White),
                                ),
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(12.dp))

                // 주간 캘린더 헤더 (MON TUE WED ...)
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Horizontal.Start,
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                ) {
                    weekDates.forEach { date ->
                        Box(
                            modifier = GlanceModifier.defaultWeight(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = date.dayOfWeek.getDisplayName(SHORT, KOREAN).uppercase(),
                                style =
                                    TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color =
                                            ColorProvider(
                                                when (date.dayOfWeek) {
                                                    DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> Color(0xFFE67C73)
                                                    else -> Color.DarkGray
                                                },
                                            ),
                                    ),
                            )
                        }
                    }
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                // 주간 날짜 (12 13 14 ...)
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Horizontal.Start,
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                ) {
                    weekDates.forEach { date ->
                        val isToday = date == today
                        val isSelected = date == selectedDate

                        Box(
                            modifier = GlanceModifier.defaultWeight(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier =
                                    GlanceModifier
                                        .width(32.dp)
                                        .height(32.dp)
                                        .then(
                                            when {
                                                isToday -> GlanceModifier.background(ImageProvider(R.drawable.widget_circle_blue))
                                                isSelected ->
                                                    GlanceModifier.background(
                                                        ImageProvider(R.drawable.widget_selected_date_background),
                                                    )

                                                else -> GlanceModifier
                                            },
                                        ).clickable(
                                            actionRunCallback<NavigateDateCallback>(
                                                actionParametersOf(
                                                    NavigateDateCallback.currentDateKey to selectedDate.toString(),
                                                    NavigateDateCallback.targetDateKey to date.toString(),
                                                ),
                                            ),
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    style =
                                        TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                            color =
                                                ColorProvider(
                                                    when {
                                                        isToday -> Color.White
                                                        else -> Color.Black
                                                    },
                                                ),
                                        ),
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier =
                        GlanceModifier
                            .fillMaxWidth()
                            .height(12.dp),
                )

                Spacer(
                    modifier =
                        GlanceModifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFCCCCCC)),
                )

                Spacer(
                    modifier =
                        GlanceModifier
                            .fillMaxWidth()
                            .height(6.dp),
                )

                // 일정 목록 (스크롤 가능)
                if (selectedDateSchedules.isEmpty()) {
                    Box(
                        modifier =
                            GlanceModifier
                                .fillMaxWidth()
                                .defaultWeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "일정 없음",
                            style =
                                TextStyle(
                                    fontSize = 14.sp,
                                    color = ColorProvider(Color.Gray),
                                ),
                        )
                    }
                } else {
                    LazyColumn(
                        modifier =
                            GlanceModifier
                                .fillMaxWidth()
                                .defaultWeight(),
                    ) {
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
        val indicatorDrawable: Int,
    )

    @Composable
    private fun ScheduleWidgetItem(schedule: PersonalScheduleUiModel) {
        val scheduleWidgetItem =
            when (schedule) {
                is CourseScheduleUiModel ->
                    ScheduleWidgetUiModel(
                        schedule.courseName.ifEmpty { schedule.title },
                        schedule.endAt.formatTimeWithMidnightSpecialCase() + " 까지",
                        R.drawable.widget_schedule_indicator_green,
                    )

                is CustomScheduleUiModel ->
                    ScheduleWidgetUiModel(
                        schedule.title,
                        schedule.endAt.formatTimeWithMidnightSpecialCase() + " 까지",
                        R.drawable.widget_schedule_indicator_red,
                    )
            }

        val intent =
            Intent().apply {
                setClassName(
                    "pusan.university.plato_calendar",
                    "pusan.university.plato_calendar.presentation.PlatoCalendarActivity",
                )
                putExtra(AlarmScheduler.EXTRA_SCHEDULE_ID, schedule.id)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

        Row(
            modifier =
                GlanceModifier
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
                        .background(ImageProvider(scheduleWidgetItem.indicatorDrawable)),
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
