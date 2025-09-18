package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import java.time.LocalDate

const val MAX_MONTH_SIZE = 12
private const val MAX_WEEK_SIZE = 6
private const val MAX_DAY_SIZE = 7

@Composable
fun Calendar(
    pagerState: PagerState,
    today: LocalDate,
    selectedDate: LocalDate,
    currentYearMonth: YearMonth,
    schedules: List<ScheduleUiModel>,
    onClickDate: (LocalDate) -> Unit,
    onSwipeMonth: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    var previousPage by remember { mutableIntStateOf(0) }

    LaunchedEffect(pagerState.currentPage) {
        if (!pagerState.isScrollInProgress) {
            previousPage = 0
        }

        val diff = pagerState.currentPage - previousPage
        if (diff != 0) {
            val yearMonth = calculateYearMonth(currentYearMonth, pagerState.currentPage, previousPage)
            onSwipeMonth(yearMonth)
            previousPage = pagerState.currentPage
        }
    }

    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
        modifier = modifier,
    ) { page ->
        val yearMonth = calculateYearMonth(currentYearMonth, page, previousPage)
        val monthDates by remember { mutableStateOf(calculateMonthDates(yearMonth)) }

        Column {
            DayOfWeekHeader(modifier = Modifier.padding(top = 4.dp))

            MonthItem(
                monthDates = monthDates,
                today = today,
                selectedDate = selectedDate,
                currentYearMonth = yearMonth,
                schedules = schedules,
                onClickDate = onClickDate,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun calculateMonthDates(yearMonth: YearMonth): List<List<LocalDate>> {
    val currentDate = LocalDate.of(yearMonth.year, yearMonth.month, 1)

    val dayOfWeekValue = if (currentDate.dayOfWeek.value == 7) 0 else currentDate.dayOfWeek.value
    val firstWeekStart = currentDate.minusDays(dayOfWeekValue.toLong())

    val monthDates = mutableListOf<List<LocalDate>>()

    repeat(MAX_WEEK_SIZE) { weekIndex ->
        val week = mutableListOf<LocalDate>()

        repeat(MAX_DAY_SIZE) { dayIndex ->
            val date = firstWeekStart.plusDays((weekIndex * MAX_DAY_SIZE + dayIndex).toLong())
            week.add(date)
        }
        monthDates.add(week)
    }

    return monthDates
}

private fun calculateYearMonth(
    baseYearMonth: YearMonth,
    page: Int,
    previousPage: Int,
): YearMonth {
    val diff = page - previousPage
    var newYear = baseYearMonth.year
    var newMonth = baseYearMonth.month + diff

    while (newMonth > 12) {
        newMonth -= 12
        newYear += 1
    }

    while (newMonth < 1) {
        newMonth += 12
        newYear -= 1
    }

    return YearMonth(newYear, newMonth)
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    val today = LocalDate.now()
    PlatoCalendarTheme {
        Calendar(
            pagerState = rememberPagerState(initialPage = 0, pageCount = { 12 }),
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
