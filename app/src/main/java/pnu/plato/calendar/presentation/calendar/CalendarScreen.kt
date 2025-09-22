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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.component.Calendar
import pnu.plato.calendar.presentation.calendar.component.CalendarTopBar
import pnu.plato.calendar.presentation.calendar.component.MAX_MONTH_SIZE
import pnu.plato.calendar.presentation.calendar.component.ScheduleBottomSheet
import pnu.plato.calendar.presentation.calendar.component.SelectedDateScheduleInfo
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateCurrentYearMonth
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateSchedules
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateSelectedDate
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.calendar.model.DaySchedule
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.extension.noRippleClickable
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.common.theme.White
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
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
            pageCount = { if (today.dayOfMonth != 1) MAX_MONTH_SIZE else MAX_MONTH_SIZE - 1 },
        )
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                else -> Unit // TODO
            }
        }
    }

    LaunchedEffect(state.schedules) {
        viewModel.setEvent(UpdateSchedules)
    }

    CalendarContent(
        state = state,
        pagerState = pagerState,
        sheetState = sheetState,
        coroutineScope = coroutineScope,
        getMonthSchedule = viewModel::getMonthSchedule,
        onEvent = viewModel::setEvent,
        modifier = modifier.background(White),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarContent(
    state: CalendarState,
    pagerState: PagerState,
    sheetState: SheetState,
    coroutineScope: CoroutineScope,
    getMonthSchedule: (YearMonth) -> List<SnapshotStateList<DaySchedule?>>,
    onEvent: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        CalendarTopBar(
            selectedDate = state.selectedDate,
            currentYearMonth = state.currentYearMonth,
            moveToToday = {
                onEvent(MoveToToday)
                coroutineScope.launch { pagerState.scrollToPage(0) }
            },
            showMakePersonalScheduleBottomSheet = {},
            modifier =
                Modifier
                    .background(PrimaryColor)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(50.dp),
        )

        Calendar(
            pagerState = pagerState,
            getMonthSchedule = getMonthSchedule,
            onDateClick = { date -> onEvent(UpdateSelectedDate(date)) },
            onMonthSwipe = { yearMonth -> onEvent(UpdateCurrentYearMonth(yearMonth)) },
            modifier = Modifier.fillMaxWidth(),
        )

        SelectedDateScheduleInfo(
            selectedDate = state.selectedDate,
            schedules = state.selectedDateSchedules,
            onScheduleClick = { schedule ->
                coroutineScope.launch { sheetState.show() }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 8.dp),
        )
    }

    if (state.isLoading) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .noRippleClickable(),
        ) {
            CircularProgressIndicator(
                color = PrimaryColor,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }

    ScheduleBottomSheet(
        schedule = state.selectedSchedule,
        sheetState = sheetState,
        onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
        modifier = Modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    PlatoCalendarTheme {
        val base = LocalDate.of(2024, 1, 1)
        val schedules =
            listOf(
                AcademicScheduleUiModel(
                    title = "신정",
                    startAt = LocalDate.of(2024, 1, 11),
                    endAt = LocalDate.of(2024, 1, 11),
                ),
                PersonalScheduleUiModel(
                    id = 1L,
                    title = "새해 계획 세우기",
                    description = "",
                    startAt = LocalDateTime.of(2024, 1, 11, 14, 0),
                    endAt = LocalDateTime.of(2024, 1, 11, 16, 0),
                ),
            )

        val monthSchedule =
            List(6) { week ->
                List<DaySchedule?>(7) { day ->
                    val date = base.minusDays(1).plusDays((week * 7 + day).toLong())
                    DaySchedule(
                        date = date,
                        isToday = date.dayOfMonth == 8,
                        isSelected = date.dayOfMonth == 11,
                        isInMonth = date.monthValue == 1,
                        schedules = schedules,
                    )
                }.toMutableStateList()
            }

        CalendarContent(
            state =
                CalendarState(
                    selectedDate = LocalDate.of(2024, 1, 11),
                    schedules = schedules,
                ),
            pagerState = rememberPagerState(initialPage = 0, pageCount = { 12 }),
            sheetState = rememberModalBottomSheetState(),
            coroutineScope = rememberCoroutineScope(),
            getMonthSchedule = { yearMonth -> monthSchedule },
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
