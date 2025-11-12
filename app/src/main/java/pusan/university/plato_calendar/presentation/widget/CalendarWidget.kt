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
import androidx.glance.layout.fillMaxHeight
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
import java.time.LocalDate
import java.time.YearMonth
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

            val currentMonth = YearMonth.of(today.year, today.month)
            val schedules = ScheduleSerializer.deserializeSchedules(schedulesJson)
            val schedulesMap = groupSchedulesByDate(schedules)
            val selectedDateSchedules = schedulesMap[selectedDate] ?: emptyList()

            Row(
                modifier =
                    GlanceModifier
                        .fillMaxSize()
                        .appWidgetBackground()
                        .cornerRadius(24.dp)
                        .background(Color.White),
            ) {
                // 왼쪽: 선택된 날짜의 일정 목록
                Column(
                    modifier =
                        GlanceModifier
                            .defaultWeight()
                            .fillMaxHeight()
                            .background(Color(0xFFF5F5F5))
                            .padding(12.dp),
                    verticalAlignment = Alignment.Vertical.Top,
                ) {
                    // 선택된 날짜 표시
                    Text(
                        text = selectedDate.dayOfMonth.toString(),
                        style =
                            TextStyle(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.glance.unit.ColorProvider(Color.Black),
                            ),
                    )
                    Text(
                        text = selectedDate.dayOfWeek.toString().substring(0, 3),
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = androidx.glance.unit.ColorProvider(Color.Gray),
                            ),
                    )

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
                                )
                            }
                        } else {
                            items(selectedDateSchedules) { schedule ->
                                ScheduleItem(schedule)
                                Spacer(modifier = GlanceModifier.height(8.dp))
                            }
                        }
                    }

                    Spacer(modifier = GlanceModifier.defaultWeight())

                    // 새로고침 버튼
                    Button(
                        text = "🔄",
                        onClick = actionRunCallback<RefreshSchedulesCallback>(),
                        modifier = GlanceModifier.fillMaxWidth(),
                    )
                }

                // 구분선
                Spacer(
                    modifier =
                        GlanceModifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(Color(0xFFE0E0E0)),
                )

                // 오른쪽: 캘린더
                Column(
                    modifier =
                        GlanceModifier
                            .defaultWeight()
                            .fillMaxHeight()
                            .padding(12.dp),
                    verticalAlignment = Alignment.Vertical.Top,
                ) {
                    // 월 표시
                    Text(
                        text = "${currentMonth.month} ${currentMonth.year}",
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.glance.unit.ColorProvider(Color.Black),
                            ),
                        modifier = GlanceModifier.fillMaxWidth(),
                    )

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    // 요일 헤더
                    Row(modifier = GlanceModifier.fillMaxWidth()) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                            Text(
                                text = day,
                                modifier = GlanceModifier.defaultWeight(),
                                style =
                                    TextStyle(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color =
                                            androidx.glance.unit.ColorProvider(
                                                when (day) {
                                                    "S" -> Color.Red
                                                    else -> Color.Gray
                                                },
                                            ),
                                    ),
                            )
                        }
                    }

                    Spacer(modifier = GlanceModifier.height(4.dp))

                    // 캘린더 그리드
                    val dates = getDatesForMonth(currentMonth.toString())

                    dates.chunked(7).forEach { week ->
                        Row(
                            modifier = GlanceModifier.fillMaxWidth().height(32.dp),
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                            verticalAlignment = Alignment.Vertical.CenterVertically,
                        ) {
                            week.forEach { date ->
                                if (date != null) {
                                    val isToday = date == today
                                    val isSelected = date == selectedDate
                                    val hasSchedule = schedulesMap[date]?.isNotEmpty() == true
                                    val isCurrentMonth = date.month == currentMonth.month

                                    Column(
                                        modifier =
                                            GlanceModifier
                                                .defaultWeight()
                                                .clickable(
                                                    actionRunCallback<SelectDateCallback>(
                                                        actionParametersOf(
                                                            SelectDateCallback.dateKey to date.toString(),
                                                        ),
                                                    ),
                                                ).padding(2.dp),
                                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                                        verticalAlignment = Alignment.Vertical.CenterVertically,
                                    ) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            style =
                                                TextStyle(
                                                    fontSize = 11.sp,
                                                    fontWeight =
                                                        if (isToday || isSelected) {
                                                            FontWeight.Bold
                                                        } else {
                                                            FontWeight.Normal
                                                        },
                                                    color =
                                                        androidx.glance.unit.ColorProvider(
                                                            when {
                                                                isSelected -> Color.White
                                                                isToday -> Color.Blue
                                                                !isCurrentMonth -> Color.LightGray
                                                                date.dayOfWeek.value == 7 -> Color.Red
                                                                date.dayOfWeek.value == 6 -> Color.Blue
                                                                else -> Color.Black
                                                            },
                                                        ),
                                                ),
                                            modifier =
                                                if (isSelected) {
                                                    GlanceModifier
                                                        .background(Color.Blue)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                } else if (isToday) {
                                                    GlanceModifier
                                                        .background(Color(0xFFE3F2FD))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                } else {
                                                    GlanceModifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                },
                                        )
                                        if (hasSchedule && !isSelected) {
                                            Text(
                                                text = "•",
                                                style =
                                                    TextStyle(
                                                        fontSize = 6.sp,
                                                        color = androidx.glance.unit.ColorProvider(Color.Red),
                                                    ),
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = GlanceModifier.defaultWeight())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ScheduleItem(schedule: ScheduleUiModel) {
        val (title, time, color) =
            when (schedule) {
                is AcademicScheduleUiModel -> Triple(schedule.title, "", Color(0xFF9575CD))
                is CourseScheduleUiModel ->
                    Triple(
                        schedule.courseName.ifEmpty { schedule.title },
                        schedule.endAt.format(DateTimeFormatter.ofPattern("HH:mm")),
                        if (schedule.isCompleted) Color.Gray else Color(0xFF81C784),
                    )

                is CustomScheduleUiModel ->
                    Triple(
                        schedule.title,
                        schedule.endAt.format(DateTimeFormatter.ofPattern("HH:mm")),
                        if (schedule.isCompleted) Color.Gray else Color(0xFFE57373),
                    )
            }

        Row(
            modifier =
                GlanceModifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Spacer(
                modifier =
                    GlanceModifier
                        .width(4.dp)
                        .height(32.dp)
                        .background(color),
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style =
                        TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = androidx.glance.unit.ColorProvider(Color.Black),
                        ),
                    maxLines = 1,
                )
                if (time.isNotEmpty()) {
                    Text(
                        text = time,
                        style =
                            TextStyle(
                                fontSize = 11.sp,
                                color = androidx.glance.unit.ColorProvider(Color.Gray),
                            ),
                    )
                }
            }
        }
    }

    private fun getDatesForMonth(yearMonthStr: String): List<LocalDate?> {
        val yearMonth =
            try {
                YearMonth.parse(yearMonthStr)
            } catch (e: Exception) {
                YearMonth.now()
            }

        val firstDayOfMonth = yearMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 일요일을 0으로
        val daysInMonth = yearMonth.lengthOfMonth()

        val dates = mutableListOf<LocalDate?>()

        // 이전 달의 빈 칸
        repeat(firstDayOfWeek) {
            val prevMonthDay = firstDayOfMonth.minusDays((firstDayOfWeek - it).toLong())
            dates.add(prevMonthDay)
        }

        // 현재 달의 날짜들
        for (day in 1..daysInMonth) {
            dates.add(yearMonth.atDay(day))
        }

        // 다음 달 날짜로 채우기 (최대 6주)
        val lastDay = yearMonth.atDay(daysInMonth)
        var nextDay = 1
        while (dates.size < 42) {
            dates.add(lastDay.plusDays(nextDay.toLong()))
            nextDay++
        }

        return dates
    }

    private fun groupSchedulesByDate(schedules: List<ScheduleUiModel>): Map<LocalDate, List<ScheduleUiModel>> =
        schedules.groupBy { schedule ->
            when (schedule) {
                is AcademicScheduleUiModel -> schedule.endAt
                is PersonalScheduleUiModel -> schedule.endAt.toLocalDate()
            }
        }
}
