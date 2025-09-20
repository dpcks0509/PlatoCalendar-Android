package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.model.DaySchedule
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import java.time.LocalDate

const val MAX_MONTH_SIZE = 12
const val MAX_WEEK_SIZE = 6
const val MAX_DAY_SIZE = 7

@Composable
fun Calendar(
    pagerState: PagerState,
    getMonthSchedule: (YearMonth) -> List<SnapshotStateList<DaySchedule>>,
    onClickDate: (LocalDate) -> Unit,
    onSwipeMonth: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val baseYearMonth = YearMonth(today.year, today.monthValue)

    LaunchedEffect(pagerState.currentPage) {
        val yearMonth = baseYearMonth.plusMonths(pagerState.currentPage)
        onSwipeMonth(yearMonth)
    }

    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
        modifier = modifier,
    ) { page ->
        val yearMonth = baseYearMonth.plusMonths(page)
        val monthSchedule = getMonthSchedule(yearMonth)

        Column {
            DayOfWeekHeader(modifier = Modifier.padding(top = 4.dp))

            MonthItem(
                monthSchedule = monthSchedule,
                onClickDate = onClickDate,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    PlatoCalendarTheme {
        val today = LocalDate.of(2024, 1, 8)

        val monthlyDates: Map<YearMonth, List<List<LocalDate>>> =
            (0 until 12).associate { monthOffset ->
                val yearMonth = YearMonth(2024, monthOffset + 1)
                val monthDate = List(MAX_WEEK_SIZE) { week ->
                    List(MAX_DAY_SIZE) { day ->
                        LocalDate.of(2024, monthOffset + 1, 1).minusDays(1)
                            .plusDays((week * MAX_DAY_SIZE + day).toLong())
                    }
                }
                yearMonth to monthDate
            }

        Calendar(
            pagerState = rememberPagerState(initialPage = 0, pageCount = { 12 }),
            getMonthSchedule = { yearMonth ->
                val dates = monthlyDates[yearMonth].orEmpty()
                dates.map { week ->
                    week.map { date ->
                        DaySchedule(
                            date = date,
                            isToday = date == today,
                            isSelected = date == today,
                            isInMonth = date.monthValue == yearMonth.month && date.year == yearMonth.year,
                            schedules = emptyList(),
                        )
                    }.toMutableStateList()
                }
            },
            onClickDate = {},
            onSwipeMonth = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}