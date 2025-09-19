package pnu.plato.calendar.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import pnu.plato.calendar.presentation.calendar.component.Calendar
import pnu.plato.calendar.presentation.calendar.component.CalendarTopBar
import pnu.plato.calendar.presentation.calendar.component.MAX_MONTH_SIZE
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ChangeCurrentYearMonth
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ChangeSelectedDate
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect.ScrollToFirstMonth
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.extension.noRippleClickable
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun CalendarScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = { MAX_MONTH_SIZE },
        )

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                ScrollToFirstMonth -> pagerState.scrollToPage(0)
            }
        }
    }

//    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
//        viewModel.setEvent(CalendarEvent.GetPersonalSchedules)
//    }

    CalendarContent(
        state = state,
        pagerState = pagerState,
        monthlyDates = viewModel.monthlyDates,
        onEvent = viewModel::setEvent,
        modifier = modifier,
    )
}

@Composable
fun CalendarContent(
    state: CalendarState,
    pagerState: PagerState,
    monthlyDates: Map<YearMonth, List<List<LocalDate>>>,
    onEvent: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        CalendarTopBar(
            today = state.today,
            selectedDate = state.selectedDate,
            currentYearMonth = state.currentYearMonth,
            moveToToday = { onEvent(MoveToToday) },
            showMakePersonalScheduleBottomSheet = {},
            modifier =
                Modifier
                    .background(PrimaryColor)
                    .statusBarsPadding()
                    .padding(all = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp),
        )

        Calendar(
            pagerState = pagerState,
            today = state.today,
            selectedDate = state.selectedDate,
            currentYearMonth = state.currentYearMonth,
            schedules = state.schedules,
            monthlyDates = monthlyDates,
            onClickDate = { date -> onEvent(ChangeSelectedDate(date)) },
            onSwipeMonth = { yearMonth -> onEvent(ChangeCurrentYearMonth(yearMonth)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (state.isLoading) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .noRippleClickable(),
        ) {
            CircularProgressIndicator(color = PrimaryColor, modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    PlatoCalendarTheme {
        val monthlyDates: Map<YearMonth, List<List<LocalDate>>> =
            (0 until 12).associate { monthOffset ->
                val yearMonth = YearMonth(2024, monthOffset + 1)
                val monthDates = List(6) { week ->
                    List(7) { day ->
                        LocalDate.of(2024, monthOffset + 1, 1).minusDays(1)
                            .plusDays((week * 7 + day).toLong())
                    }
                }
                yearMonth to monthDates
            }

        val schedules = listOf(
            AcademicScheduleUiModel(
                title = "신정",
                startAt = LocalDate.of(2024, 1, 1),
                endAt = LocalDate.of(2024, 1, 1),
            ),
            PersonalScheduleUiModel(
                id = 1L,
                title = "새해 계획 세우기",
                description = "",
                startAt = LocalDateTime.of(2024, 1, 3, 14, 0),
                endAt = LocalDateTime.of(2024, 1, 3, 16, 0),
                courseName = null,
            ),
        )

        CalendarContent(
            state = CalendarState(
                today = LocalDate.of(2024, 1, 8),
                selectedDate = LocalDate.of(2024, 1, 11),
                schedules = schedules
            ),
            pagerState = rememberPagerState(initialPage = 0, pageCount = { 12 }),
            monthlyDates = monthlyDates,
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}