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
const val MAX_WEEK_SIZE = 6
const val MAX_DAY_SIZE = 7

@Composable
fun Calendar(
    pagerState: PagerState,
    today: LocalDate,
    selectedDate: LocalDate,
    currentYearMonth: YearMonth,
    schedules: List<ScheduleUiModel>,
    monthlyDates: Map<YearMonth, List<List<LocalDate>>>,
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
            val yearMonth =
                calculateYearMonth(currentYearMonth, pagerState.currentPage, previousPage)
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
        val monthDates = monthlyDates[yearMonth] ?: emptyList()

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
            monthlyDates = mapOf(),
            onClickDate = {},
            onSwipeMonth = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
