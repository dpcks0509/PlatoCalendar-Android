package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pnu.plato.calendar.presentation.calendar.model.DayUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import java.time.LocalDate

private const val MAX_MONTH_SIZE = 12
private const val MAX_WEEK_SIZE = 6
private const val MAX_DAY_SIZE = 7

@Composable
fun Calendar(
    today: LocalDate,
    selectedDate: LocalDate,
    currentYearMonth: YearMonth,
    schedules: List<ScheduleUiModel>,
    onClickDate: (LocalDate) -> Unit,
    onSwipeMonth: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = { MAX_MONTH_SIZE },
        )

    var previousPage by remember { mutableIntStateOf(0) }

    // TODO 고치기
    LaunchedEffect(pagerState.currentPage) {
        val diff = pagerState.currentPage - previousPage

        if (diff != 0) {
            var newYear = currentYearMonth.year
            var newMonth = currentYearMonth.month + diff

            if (newMonth > 12) {
                newMonth = 1
                newYear += 1
            } else if (newMonth < 1) {
                newMonth = 12
                newYear -= 1
            }

            onSwipeMonth(YearMonth(newYear, newMonth))
            previousPage = pagerState.currentPage
        }
    }

    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
        modifier = modifier,
    ) { page ->
        val diff = page - previousPage

        if (diff != 0) {
            var newYear = currentYearMonth.year
            var newMonth = currentYearMonth.month + diff

            if (newMonth > 12) {
                newMonth = 1
                newYear += 1
            } else if (newMonth < 1) {
                newMonth = 12
                newYear -= 1
            }

            onSwipeMonth(YearMonth(newYear, newMonth))
            previousPage = page
        }

        val month =
            createMonth(
                currentYearMonth = currentYearMonth,
                today = today,
                selectedDate = selectedDate,
                schedules = schedules,
            )

        Column {
            DayOfWeekHeader(modifier = Modifier.padding(top = 4.dp))

            MonthItem(
                month = month,
                onClickDate = onClickDate,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun createMonth(
    currentYearMonth: YearMonth,
    today: LocalDate,
    selectedDate: LocalDate,
    schedules: List<ScheduleUiModel>,
): List<List<DayUiModel>> {
    val currentDate = LocalDate.of(currentYearMonth.year, currentYearMonth.month, 1)

    val dayOfWeekValue = if (currentDate.dayOfWeek.value == 7) 0 else currentDate.dayOfWeek.value
    val firstWeekStart = currentDate.minusDays(dayOfWeekValue.toLong())

    val weeks = mutableListOf<List<DayUiModel>>()

    repeat(MAX_WEEK_SIZE) { weekIndex ->
        val week = mutableListOf<DayUiModel>()

        repeat(MAX_DAY_SIZE) { dayIndex ->
            val currentDate = firstWeekStart.plusDays((weekIndex * MAX_DAY_SIZE + dayIndex).toLong())
            val isInMonth = currentDate.monthValue == currentYearMonth.month && currentDate.year == currentYearMonth.year
            val daySchedules =
                schedules.filter { schedule ->
                    when (schedule) {
                        is ScheduleUiModel.AcademicScheduleUiModel -> {
                            currentDate == schedule.endAt
                        }

                        is ScheduleUiModel.PersonalScheduleUiModel -> {
                            currentDate == schedule.endAt.toLocalDate()
                        }
                    }
                }

            week.add(
                DayUiModel(
                    date = currentDate,
                    isToday = currentDate == today,
                    isSelected = currentDate == selectedDate,
                    isInMonth = isInMonth,
                    schedules = daySchedules,
                ),
            )
        }
        weeks.add(week)
    }

    return weeks
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    val today = LocalDate.now()
    PlatoCalendarTheme {
        Calendar(
            today = today,
            selectedDate = today,
            currentYearMonth = YearMonth(today.year, today.monthValue),
            schedules = listOf(),
            onClickDate = {},
            onSwipeMonth = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
